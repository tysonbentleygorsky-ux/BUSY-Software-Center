package ca.busyschool.softcenter;
import java.util.List; import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api") public class PlatformController {
 private final JdbcTemplate db; public PlatformController(JdbcTemplate db){this.db=db;}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok","service","busy-api");}
 @GetMapping("/catalog") public List<Map<String,Object>> catalog(@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="") String category){
  return db.queryForList("SELECT s.id,s.name,s.publisher,s.description,s.category,r.version,r.platform,r.sha256,r.size_bytes FROM software s JOIN software_releases r ON r.software_id=s.id WHERE s.status='PUBLISHED' AND r.status='PUBLISHED' AND (?='' OR s.name ILIKE '%'||?||'%' OR s.publisher ILIKE '%'||?||'%') AND (?='' OR s.category ILIKE ?) ORDER BY s.name,r.platform",q,q,q,category,category);
 }
 @GetMapping("/admin/summary") public Map<String,Object> summary(){
  return Map.of("publishedSoftware",db.queryForObject("SELECT count(*) FROM software WHERE status='PUBLISHED'",Long.class),
   "pendingRequests",db.queryForObject("SELECT count(*) FROM software_requests WHERE status IN ('PENDING','IN_REVIEW')",Long.class),
   "publishedReleases",db.queryForObject("SELECT count(*) FROM software_releases WHERE status='PUBLISHED'",Long.class));
 }
}
