const { app, BrowserWindow, dialog, shell } = require('electron');
const fs = require('fs');
const path = require('path');
const { startServer } = require('./server');
let win, server, startupLogPath;

function logStartup(message) {
  try {
    startupLogPath = startupLogPath || path.join(app.getPath('userData'), 'startup.log');
    fs.mkdirSync(path.dirname(startupLogPath), { recursive: true });
    fs.appendFileSync(startupLogPath, '[' + new Date().toISOString() + '] ' + message + '\n');
  } catch (_) {}
}

function showStartupFailure(error) {
  const details = error && error.stack ? error.stack : String(error);
  logStartup('Startup failed: ' + details);
  const location = startupLogPath ? '\n\nStartup log: ' + startupLogPath : '';
  dialog.showErrorBox('Could not start Software Center', (error && error.message ? error.message : String(error)) + location);
  app.quit();
}

async function createWindow() {
  app.setName('BUSY School Software Center');
  logStartup('Starting Software Center.');
  const dataFile = path.join(app.getPath('userData'), 'data.json');
  logStartup('User data folder: ' + path.dirname(dataFile));
  server = await startServer({ dataFile });
  logStartup('Local data service started at ' + server.url + '.');
  win = new BrowserWindow({
    width: 1440,
    height: 940,
    minWidth: 1000,
    minHeight: 700,
    title: 'BUSY School Software Center',
    backgroundColor: '#f4f8fe',
    webPreferences: { contextIsolation: true, nodeIntegration: false, sandbox: true }
  });
  win.webContents.setWindowOpenHandler(({ url }) => {
    if (url.startsWith(server.url)) return { action: 'allow' };
    shell.openExternal(url);
    return { action: 'deny' };
  });
  win.webContents.on('did-fail-load', (_event, code, description, url, mainFrame) => {
    if (mainFrame) logStartup('Page load failed (' + code + '): ' + description + ' [' + url + ']');
  });
  win.webContents.on('render-process-gone', (_event, details) => {
    logStartup('Renderer process exited: ' + JSON.stringify(details));
  });
  win.on('closed', () => { win = null; });
  await win.loadURL(server.url);
  logStartup('Main window loaded successfully.');
}

app.whenReady().then(createWindow).catch(showStartupFailure);
process.on('uncaughtException', error => logStartup('Uncaught exception: ' + (error && error.stack ? error.stack : error)));
process.on('unhandledRejection', reason => logStartup('Unhandled rejection: ' + (reason && reason.stack ? reason.stack : reason)));
app.on('window-all-closed', () => {
  if (server) server.close();
  if (process.platform !== 'darwin') app.quit();
});
app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) createWindow().catch(showStartupFailure);
});
