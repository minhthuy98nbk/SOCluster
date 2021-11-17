package com.zingplay.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/test")
public class TestObjectController {

    //@Autowired
    //TestObjectRepository testObjectRepository;
    //
    //@GetMapping("")
    //public ResponseEntity<?> get(
    //        @RequestParam(required = false) String search,
    //        @PageableDefault(page = 0,size = 10)
    //        @SortDefault.SortDefaults({
    //                @SortDefault(sort = "name", direction = Sort.Direction.ASC)
    //        })
    //                Pageable pageable
    //){
    //    Page<TestObject> page;
    //    if(search!=null && !search.isEmpty()){
    //        page = testObjectRepository.findByNameContaining(search, pageable);
    //    }else{
    //        page = testObjectRepository.findAll(pageable);
    //    }
    //    return ResponseEntity.ok(page);
    //}
    //
    //@GetMapping("all")
    //public ResponseEntity<?>getAll(){
    //    List<TestObject> clusterList = testObjectRepository.findAll();
    //    return ResponseEntity.ok(clusterList);
    //}
    //
    //
    //@PostMapping("")
    //public ResponseEntity<?> create(@RequestBody TestObject Cluster){
    //    //Set<Permission> permissions = Cluster.getPermissions();
    //    //Set<Permission> permissionsUpdate = new HashSet<>();
    //    //for (Permission permission : permissions) {
    //    //    Permission p = permissionRepository.findById(permission.getId())
    //    //            .orElseThrow(() -> new RuntimeException("Error: Cluster is not found."));
    //    //    permissionsUpdate.add(p);
    //    //}
    //    //Cluster.setPermissions(permissionsUpdate);
    //    TestObject save = testObjectRepository.save(Cluster);
    //    return ResponseEntity.ok(save);
    //}
    //
    //@PutMapping("{id}")
    //public ResponseEntity<?> update(@RequestBody TestObject cluster, @PathVariable String id){
    //    TestObject save =  testObjectRepository.findById(id).map(clusterUpdate -> {
    //        clusterUpdate.setName(cluster.getName());
    //        return testObjectRepository.save(clusterUpdate);
    //    }).orElseGet(()->{
    //        cluster.setId(id);
    //        return testObjectRepository.save(cluster);
    //    });
    //    return ResponseEntity.ok(save);
    //}
    //
    //@DeleteMapping("{id}")
    //public ResponseEntity<?> delete(@PathVariable String id){
    //    testObjectRepository.deleteById(id);
    //    return ResponseEntity.ok(new MessageResponse("TestObject deleted successfully!"));
    //}
}
