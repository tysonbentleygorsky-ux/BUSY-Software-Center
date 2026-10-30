const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { fileURLToPath } = require('node:url');
const { spawnSync } = require('node:child_process');

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const dist = path.join(root, 'dist');
const version = JSON.parse(fs.readFileSync(path.join(root, 'package.json'), 'utf8')).version;

function run(command, args, cwd = root) {
  const result = spawnSync(command, args, {
    cwd,
    stdio: 'inherit',
    shell: process.platform === 'win32'
  });
  if (result.error) throw result.error;
  if (result.status !== 0) process.exit(result.status || 1);
}

function findFiles(directory, depth, matches) {
  if (depth < 0 || !fs.existsSync(directory)) return;
  let entries;
  try {
    entries = fs.readdirSync(directory, { withFileTypes: true });
  } catch {
    return;
  }
  for (const entry of entries) {
    const fullPath = path.join(directory, entry.name);
    if (entry.isDirectory()) findFiles(fullPath, depth - 1, matches);
    else if (entry.name.toLowerCase() === 'makensis' || entry.name.toLowerCase() === 'makensis.exe') {
      matches.push(fullPath);
    }
  }
}

function locateMakensis() {
  const roots = [
    path.join(os.homedir(), 'Library', 'Caches', 'electron-builder'),
    path.join(process.env.LOCALAPPDATA || '', 'electron-builder', 'Cache'),
    path.join(process.env.LOCALAPPDATA || '', 'electron-builder', 'Cache', 'nsis'),
    path.join(root, 'node_modules', 'electron-builder')
  ].filter(Boolean);
  const matches = [];
  for (const directory of roots) findFiles(directory, 7, matches);
  const preferred = process.platform === 'darwin'
    ? matches.find(item => item.includes(path.sep + 'mac' + path.sep) && path.basename(item) === 'makensis')
    : matches.find(item => path.basename(item).toLowerCase() === 'makensis.exe');
  const compiler = preferred || matches[0];
  if (!compiler) throw new Error('NSIS makensis was not found. Run npm ci and try again.');
  return compiler;
}

run(process.platform === 'win32' ? 'npm.cmd' : 'npm', ['run', 'dist', '--', '--win', 'nsis', '--x64']);
run(process.platform === 'win32' ? 'npm.cmd' : 'npm', ['run', 'dist', '--', '--win', 'nsis', '--arm64']);

const payloadDir = path.join(dist, 'universal-payloads');
fs.mkdirSync(payloadDir, { recursive: true });
fs.copyFileSync(
  path.join(dist, 'BUSY School Software Center-' + version + '-x64.exe'),
  path.join(payloadDir, 'x64.exe')
);
fs.copyFileSync(
  path.join(dist, 'BUSY School Software Center-' + version + '-arm64.exe'),
  path.join(payloadDir, 'arm64.exe')
);

const makensis = locateMakensis();
run(makensis, ['-V2', 'installer/universal-windows.nsi']);
console.log('Created dist/BUSY School Software Center-' + version + '-universal.exe');
