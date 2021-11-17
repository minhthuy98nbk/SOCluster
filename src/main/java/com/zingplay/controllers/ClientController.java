package com.zingplay.controllers;

import com.zingplay.beans.Object;
import com.zingplay.beans.*;
import com.zingplay.cron.CronJobServiceImpl;
import com.zingplay.helpers.Helpers;
import com.zingplay.kafka.KafkaConsumerStatus;
import com.zingplay.kafka.KafkaSendingWorker;
import com.zingplay.kafka.KafkaService;
import com.zingplay.models.Condition;
import com.zingplay.models.CustomGift;
import com.zingplay.models.KafkaConsumer;
import com.zingplay.models.User;
import com.zingplay.module.objects.ConditionService;
import com.zingplay.module.report.ReportService;
import com.zingplay.module.telegram.MyPair;
import com.zingplay.payload.request.*;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.repository.KafkaConsumerRepository;
import com.zingplay.service.alert.AlertGameService;
import com.zingplay.service.user.*;
import com.zingplay.socket.v1.request.UserTrackingCreate;
import com.zingplay.socket.v1.request.UserTrackingFakeBuyOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/client")
public class ClientController {

    private final UserService userService;

    private final ObjectService objectService;
    private final RunOfferService runOfferService;
    private final ScheduleObjectService scheduleObjectService;
    private final UserObjectService userObjectService;
    private final LogService logService;
    private final OfferService offerService;
    private final ConditionService conditionService;
    private final ReportService reportService;
    private final RemoveService removeService;
    private final KafkaConsumerRepository kafkaConsumerRepository;
    private final CustomDataService customDataService;
    private KafkaService kafkaService;
    private Helpers helpers;

    @Autowired
    public ClientController(UserService userService, ObjectService objectService, RunOfferService runOfferService,
                            @Lazy ScheduleObjectService scheduleObjectService, UserObjectService userObjectService,
                            LogService logService, OfferService offerService, ConditionService conditionService, ReportService reportService,
                            RemoveService removeService, KafkaConsumerRepository kafkaConsumerRepository,
                            CustomDataService customDataService, KafkaService kafkaService, Helpers helpers) {
        this.userService = userService;
        this.objectService = objectService;
        this.runOfferService = runOfferService;
        this.scheduleObjectService = scheduleObjectService;
        this.userObjectService = userObjectService;
        this.logService = logService;
        this.offerService = offerService;
        this.conditionService = conditionService;
        this.reportService = reportService;
        this.removeService = removeService;
        this.kafkaConsumerRepository = kafkaConsumerRepository;
        this.customDataService = customDataService;
        this.kafkaService = kafkaService;
        this.helpers = helpers;
    }

    @Value("${server.port}")
    public int port;

    @GetMapping("/test")
    public String test() {
        return "Connected " + port + " ...";
    }

    //region user
    @GetMapping("/user")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "userId", direction = Sort.Direction.ASC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        // System.out.println(request.getQueryString());
        return ResponseEntity.ok(userService.getAllUser(search,pageable));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUsers(@RequestBody UserTrackingCreate request) {
        // List<UserTracking> users = request.getUsers();
        // int all = userService.createAll(users);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUsers(@RequestBody UserTracking request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id,@RequestBody User user) {
        User save = userService.updateUser(id,user);
        if(save == null){
            return ResponseEntity.ok(new MessageResponse("User not found!"));
        }
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    @GetMapping("/userObjects/{id}")
    public ResponseEntity<?> getUserObjects(
            @PathVariable String id,
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "userId", direction = Sort.Direction.ASC)
            })
                    Pageable pageable){
        search = getSearchDecode(search);
        return ResponseEntity.ok(userObjectService.getAllObjects(id,search,pageable));
    }

    @GetMapping("/userOffers/{id}")
    public ResponseEntity<?> getUserOffers(
            @PathVariable String id){
        return ResponseEntity.ok(userService.getAllOffers(id));
    }

    @DeleteMapping("/userOffers/{id}/{idOffer}")
    public ResponseEntity<?> deleteUserOffers(
            @PathVariable String id,
            @PathVariable String idOffer
    ){
        //userService.deleteUserOffers(id,idOffer);
        return ResponseEntity.badRequest().body(new MessageResponse("Khong the xoa, can xoa user ra khoi object"));
    }
    @PostMapping("/userOffers")
    public ResponseEntity<?> createUserBuyOffers(@RequestBody UserTrackingFakeBuyOffer request) {
        return ResponseEntity.ok(userService.createUserBuyOffer(request));
    }

    //endregion user

    //region object
    @GetMapping("/objects")
    public ResponseEntity<?> getObjects(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "idObject", direction = Sort.Direction.ASC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(objectService.getAllObject(search,pageable));
    }

    @PostMapping("/objects")
    public ResponseEntity<?> createObjects(@RequestBody ObjectCreate request) {
        List<Object> users = request.getObjects();
        int all = objectService.createAll(users);
        return ResponseEntity.ok(all);
    }

    @PostMapping("/object")
    public ResponseEntity<?> createObject(@RequestBody Object request) {
        return ResponseEntity.ok(objectService.create(request));
    }

    @PutMapping("/object/{id}")
    public ResponseEntity<?> updateObject(@PathVariable String id,@RequestBody Object object) {
        com.zingplay.models.Object save = objectService.updateObject(id,object);
        if(save == null){
            return ResponseEntity.ok(new MessageResponse("object not found!"));
        }
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/object/{id}")
    public ResponseEntity<?> deleteObject(@PathVariable String id) {

        objectService.deleteObject(id);
        return ResponseEntity.ok(new MessageResponse("object deleted successfully!"));
    }
    //endregion object

    //region offer
    @GetMapping("/offers")
    public ResponseEntity<?> getOffers(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "idOffer", direction = Sort.Direction.ASC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);

        return ResponseEntity.ok(offerService.getAllOffer(search,pageable));
    }

    @PostMapping("/offers")
    public ResponseEntity<?> createOffers(@RequestBody OfferCreate request) {
        List<Offer> users = request.getOffers();
        int all = offerService.createAll(users);
        return ResponseEntity.ok(all);
    }

    @PostMapping("/offer")
    public ResponseEntity<?> createOffer(@RequestBody Offer request) {
        return ResponseEntity.ok(offerService.create(request));
    }

    @PutMapping("/offer/{id}")
    public ResponseEntity<?> updateOffer(@PathVariable String id,@RequestBody Offer offer) {
        com.zingplay.models.Offer save = offerService.updateOffer(id,offer);
        if(save == null){
            return ResponseEntity.ok(new MessageResponse("offer not found!"));
        }
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/offer/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable String id) {
        offerService.deleteOffer(id);
        return ResponseEntity.ok(new MessageResponse("offer deleted successfully!"));
    }
    //endregion offer

    //region runOffer
    @GetMapping("/runoffers")
    public ResponseEntity<?> getRunOffers(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeStart", direction = Sort.Direction.DESC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(runOfferService.getAllRunOffer(search,pageable));
    }

    @GetMapping("/runoffers/user/{id}")
    public ResponseEntity<?> getUserBuyRunOffers(
            @PathVariable String id,
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeStart", direction = Sort.Direction.DESC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(runOfferService.getUserBuyRunOffer(id,search,pageable));
    }

    @GetMapping("/runoffers/checkExist")
    public ResponseEntity<?> getUserBuyRunOffers(
            @RequestParam(required = true) String idRunOffer){
        return ResponseEntity.ok(runOfferService.isExistRunOffer(idRunOffer));
    }

    @GetMapping("/runoffers/getinday")
    public ResponseEntity<?> getUserBuyRunOffers(
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to,
            @PageableDefault(page = 0,size = 1000)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeStart", direction = Sort.Direction.DESC)
            })
                    Pageable pageable,HttpServletRequest request){
        return ResponseEntity.ok(runOfferService.getAllRunOfferInDay(from, to, pageable));
    }

    @PostMapping("/runoffers")
    public ResponseEntity<?> createRunOffers(@RequestBody RunOfferCreate request) {
        List<RunOffer> runOffers = request.getRunOffers();
        int all = runOfferService.createAll(runOffers);
        return ResponseEntity.ok(all);
    }

    @PostMapping("/runoffer")
    public ResponseEntity<?> createRunOffer(@RequestBody RunOffer request) {
        return runOfferService.create(request);
    }

    @PostMapping("/runoffers/createobject")
    public ResponseEntity<?> createObjectFromRunOffers(@RequestBody ObjectFromRunOfferCreate request) {
        return runOfferService.createObjectFromRunOffer(request);
    }
    @PostMapping("/runoffers/addtoobject")
    public ResponseEntity<?> addUserToObjectFromRunOffer(@RequestBody ObjectFromRunOfferCreate request) {
        return runOfferService.addUserToObjectFromRunOffer(request);
    }

    @PutMapping("/runoffer/{id}")
    public ResponseEntity<?> updateRunOffer(@PathVariable String id,@RequestBody RunOffer runOffer) {
        return runOfferService.updateRunOffer(id,runOffer);
    }

    @DeleteMapping("/runoffer/{id}")
    public ResponseEntity<?> deleteRunOffer(@PathVariable String id) {
        runOfferService.deleteRunOffer(id);
        return ResponseEntity.ok(new MessageResponse("object deleted successfully!"));
    }
    //endregion runOffer

    //region scheduleobjects
    @GetMapping("/scheduleobjects")
    public ResponseEntity<?> getScheduleObjects(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    //@SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "timeScan", direction = Sort.Direction.DESC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(scheduleObjectService.getAllScheduleObject(search,pageable));
    }
    @GetMapping("/scheduleobjects/detail/{id}")
    public ResponseEntity<?> getScheduleObjectsDetail(
            @PathVariable String id,
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    //@SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "timeScan", direction = Sort.Direction.DESC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(scheduleObjectService.getAllScheduleObjectDetail(id, search,pageable));
    }

    @PostMapping("/scheduleobject")
    public ResponseEntity<?> createScheduleObject(@RequestBody ScheduleObject request) {
        return ResponseEntity.ok(scheduleObjectService.create(request));
    }

    @PutMapping("/scheduleobject/{id}")
    public ResponseEntity<?> updateScheduleObject(@PathVariable String id,@RequestBody ScheduleObject scheduleObject) {
        com.zingplay.models.ScheduleObject save = scheduleObjectService.updateScheduleObject(id,scheduleObject);
        if(save == null){
            return ResponseEntity.ok(new MessageResponse("schedule object not found!"));
        }
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/scheduleobject/{id}")
    public ResponseEntity<?> deleteScheduleObject(@PathVariable String id) {
        scheduleObjectService.deleteScheduleObject(id);
        return ResponseEntity.ok(new MessageResponse("schedule object deleted successfully!"));
    }
    //endregion scheduleobjects

    //region user objects
    @GetMapping("/userobjects/detail/{id}")
    public ResponseEntity<?> getUserObjectsDetail(
            @PathVariable String id,
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    //@SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(userObjectService.getAllUserObjectDetail(id, search,pageable));
    }

    @PostMapping("/userobjects")
    public ResponseEntity<?> createUserObject(@RequestBody UserObject request) {
        return ResponseEntity.ok(userObjectService.createUserObject(request));
    }

    @DeleteMapping("/userobjects/{id}")
    public ResponseEntity<?> deleteUserObject(@PathVariable String id) {
        userObjectService.deleteUserObject(id);
        return ResponseEntity.ok(new MessageResponse("schedule object deleted successfully!"));
    }
    //endregion user objects

    //region report -> tab đánh giá -> tổng tiền trong ngay
    @GetMapping("/report")
    public ResponseEntity<?> getReportRunOffer(
            @RequestParam(required = false) long from,
            @RequestParam(required = false) long to){

        return ResponseEntity.ok(reportService.getReportRunOffer(from,to));
    }
    @GetMapping("/report/daily")
    public ResponseEntity<?> getReportDailyRunOffer(
            @RequestParam(required = false) long from,
            @RequestParam(required = false) long to){
        return ResponseEntity.ok(reportService.getReportDailyRunOffer(from,to));
    }
    @GetMapping("/report/tracking")
    public ResponseEntity<?> getReportTrackingRequest(
            @RequestParam(required = false) long from,
            @RequestParam(required = false) long to){
        return ResponseEntity.ok(reportService.getReportTrackingRequest(from,to));
    }

    @GetMapping("/report/trackingRaw")
    public ResponseEntity<?> getTrackingRaw(
            @RequestParam(required = false) long from,
            @RequestParam(required = false) long to){
        return ResponseEntity.ok(reportService.getReportTrackingDetail(from, to, true));
    }

    @GetMapping("/report/trackingHour")
    public ResponseEntity<?> getTrackingHour(
            @RequestParam(required = false) long from,
            @RequestParam(required = false) long to){
        return ResponseEntity.ok(reportService.getReportTrackingDetail(from, to, false));
    }
    //endregion report

    //region logs
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC)
            })
                    Pageable pageable){
        search = getSearchDecode(search);
        return ResponseEntity.ok(logService.getAllLogs(search,pageable));
    }
    //endregion logs

    //region remove all
    @DeleteMapping("/remove/object")
    public ResponseEntity<?> removeAllObject() {
        removeService.removeAllObject();
        return ResponseEntity.ok(new MessageResponse("object remove successfully!"));
    }
    @DeleteMapping("/remove/offer")
    public ResponseEntity<?> removeAllOffer() {
        removeService.removeAllOffer();
        return ResponseEntity.ok(new MessageResponse("offer remove successfully!"));
    }
    @DeleteMapping("/remove/user")
    public ResponseEntity<?> removeAllUser() {
        removeService.removeAllUser();
        return ResponseEntity.ok(new MessageResponse("user remove successfully!"));
    }
    @DeleteMapping("/remove/log")
    public ResponseEntity<?> removeAllLog() {
        removeService.removeAllLog();
        return ResponseEntity.ok(new MessageResponse("user remove successfully!"));
    }
    @DeleteMapping("/remove/all")
    public ResponseEntity<?> removeAll() {
        removeService.removeAll();
        return ResponseEntity.ok(new MessageResponse("all remove successfully!"));
    }
    @DeleteMapping("/remove/alls")
    public ResponseEntity<?> removeAlls() {
        removeService.removeAlls();
        return ResponseEntity.ok(new MessageResponse("all remove successfully!"));
    }

    @DeleteMapping("/remove/oldUser/{date}/{numRemovePerJob}/{minuteDelay}/{scheduleDelay}")
    public ResponseEntity<?> removeAllOldUser(@PathVariable int date, @PathVariable int numRemovePerJob, @PathVariable int minuteDelay, @PathVariable int scheduleDelay) {
        MyPair<Boolean, String> response = removeService. removeAllOldUser(date, numRemovePerJob, minuteDelay, scheduleDelay);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/remove/oldUser/getScheduleInfo")
    public ResponseEntity<?> getRemoveUserScheduleInfo(){
        RemoveInfo removeInfo = removeService.getRemoveUserScheduleInfo();
        if (removeInfo == null) {
            return ResponseEntity.ok(new MyPair<>(false, "Schedule remove old user is not exist"));
        }
        return ResponseEntity.ok(new MyPair<>(true, removeInfo));
    }

    @DeleteMapping("/remove/oldUser/cancelSchedule")
    public ResponseEntity<?> cancelRemoveUserSchedule(){
        boolean isCancel = removeService.cancelRemoveUserSchedule();
        if (!isCancel) {
            return ResponseEntity.ok(new MyPair<>(false, "Schedule remove old user is not exist"));
        }
        return ResponseEntity.ok(new MyPair<>(true, "Cancel schedule remove user success"));
    }

    //endregion remove all

    //region kafka topic
    @GetMapping("/kafka")
    public ResponseEntity<?> getKafkaTopic(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "topic", direction = Sort.Direction.ASC)
            })
                    Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        if(search != null && !search.isEmpty()){
            return ResponseEntity.ok(kafkaConsumerRepository.findByTopicContaining(search,pageable));
        }else{
            return ResponseEntity.ok(kafkaConsumerRepository.findAll(pageable));
        }
    }

    @PostMapping("/kafka")
    public ResponseEntity<?> createKafkaTopic(@RequestBody KafkaConsumer request) {
        KafkaConsumer kafkaConsunmer = kafkaConsumerRepository.findByTopic(request.getTopic()).orElse(null);
        if(kafkaConsunmer == null){
            KafkaConsumer save = kafkaConsumerRepository.save(request);
            if(save.getStatus() == KafkaConsumerStatus.STARTED.ordinal()){
                kafkaService.startConsumer(save.getTopic());
            }
            return ResponseEntity.ok(save);
        }
        return ResponseEntity.ok(new MessageResponse("topic was exist!"));
    }

    @PutMapping("/kafka/{id}")
    public ResponseEntity<?> updateKafkaTopic(@PathVariable String id,@RequestBody KafkaConsumer request) {
        KafkaConsumer kafkaConsumer = kafkaConsumerRepository.findById(id).orElse(null);
        if(kafkaConsumer == null){
            return ResponseEntity.ok(new MessageResponse("topic not found!"));
        }
        kafkaConsumer.setTopic(request.getTopic());
        boolean needStop = false;
        if(kafkaConsumer.getStatus() != KafkaConsumerStatus.STARTED.ordinal()){
            needStop = true;
        }
        kafkaConsumer.setStatus(request.getStatus());
        KafkaConsumer save = kafkaConsumerRepository.save(kafkaConsumer);
        if(save.getStatus() == KafkaConsumerStatus.STARTED.ordinal()){
            kafkaService.startConsumer(save.getTopic());
        }else{
            if(needStop){
                kafkaService.stopConsumer(save.getTopic());
            }
        }
        return ResponseEntity.ok(kafkaConsumer);
    }

    @DeleteMapping("/kafka/{id}")
    public ResponseEntity<?> deleteKafkaTopic(@PathVariable String id) {
        KafkaConsumer kafkaConsumer = kafkaConsumerRepository.findById(id).orElse(null);
        if(kafkaConsumer != null && kafkaConsumer.getStatus() == KafkaConsumerStatus.STARTED.ordinal()){
            kafkaService.stopConsumer(kafkaConsumer.getTopic(),true);
        }
        kafkaConsumerRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Kafka topic deleted successfully!"));
    }
    //endregion kafka topic

    //region cronProcess
    @GetMapping("/cronProcess")
    public ResponseEntity<?> getCronProcess(@RequestParam(required = false) int groupId,
                                            @PageableDefault(page = 0,size = 10)
                                            @SortDefault.SortDefaults({
                                                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                            })
                                                    Pageable pageable,HttpServletRequest request){
        Map<String, java.lang.Object> res =new HashMap<>();
        res.put("cronProcess",CronJobServiceImpl.getInstance().getCronProcess( groupId, pageable) );
        res.put("queueKafkaSize", KafkaSendingWorker.getInstance().getQueueLength());
        res.put("queueKafkaRemainSize", KafkaSendingWorker.getInstance().getRemainCapacity());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/gameLibVersion")
    public ResponseEntity<?> getLibVersion(){
        return ResponseEntity.ok(CronJobServiceImpl.getInstance().getMapGame_Version());
    }

    //endregion cronProcess
    // region alert

    @GetMapping("/alertConfig")
    public ResponseEntity<?> getAlertConfig() {
        return ResponseEntity.ok(AlertGameService.getInstance().getConfig(helpers.getGame()));
    }

    @GetMapping("/alertConfig/edit/{type}/{value}")
    public ResponseEntity<?> editAlertConfig(@PathVariable int type, @PathVariable float value) {
        return ResponseEntity.ok(AlertGameService.getInstance().updateInfo(helpers.getGame(), type, value));
    }

    // endregion alert

    private String getSearchDecode(String search){
        if(search != null && !search.isEmpty()){
            try {
                return java.net.URLDecoder.decode( search, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException ignored) { }
        }
        return search;
    }

    //region custom tracking
    //1.thêm sửa xóa điều kiện custom
    //2.tạo,edit,xóa object với điều kiện custom
    //3.handler tracking từ socket game.
    @GetMapping("/trackingcfg")
    public ResponseEntity<?> getTrackingConfigs(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "idOffer", direction = Sort.Direction.ASC)
            }) Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(conditionService.getAllCondition(search,pageable));
    }
    @PostMapping("/trackingcfgs")
    public ResponseEntity<?> createTrackingConfigs(@RequestBody List<Condition> requests) {
        return conditionService.createAll(requests);
    }
    @PostMapping("/trackingcfg")
    public ResponseEntity<?> createTrackingConfig(@RequestBody Condition condition) {
        return conditionService.create(condition);
    }
    @PutMapping("/trackingcfg/{id}")
    public ResponseEntity<?> updateTrackingConfig(@PathVariable String id,@RequestBody Condition condition) {
        return conditionService.updateCondition(id,condition);
    }
    @DeleteMapping("/trackingcfg/{id}")
    public ResponseEntity<?> deleteTrackingConfig(@PathVariable String id) {
        return conditionService.deleteCondition(id);
    }

    @GetMapping("/objectcustoms")
    public ResponseEntity<?> getObjectCustomConditions(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "idObject", direction = Sort.Direction.ASC)
            }) Pageable pageable,HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok(objectService.getAllObject(search,pageable));
    }
    @PostMapping("/objectcustoms")
    public ResponseEntity<?> createObjectCustomConditions(@RequestBody List<ObjectCustomCondition> request) {
        return objectService.createCustomConditions(request);

    }
    @PostMapping("/objectcustom")
    public ResponseEntity<?> createObjectCustomCondition(@RequestBody ObjectCustomCondition request) {
        return objectService.createCustomCondition(request);
    }
    @PutMapping("/objectcustom/{id}")
    public ResponseEntity<?> updateCustomCondition(@PathVariable String id,@RequestBody ObjectCustomCondition request) {
        return objectService.updateCustomCondition(id, request);
    }
    @DeleteMapping("/objectcustom/{id}")
    public ResponseEntity<?> deleteCustomCondition(@PathVariable String id) {
        return objectService.deleteObject(id);
    }
    //endregion custom tracking

    // region custom gift
    @GetMapping("/gift")
    public ResponseEntity<?> getListCustomGift(
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0,size = 10)
            @SortDefault.SortDefaults({
                @SortDefault(sort = "timeCreate", direction = Sort.Direction.DESC),
                @SortDefault(sort = "idObject", direction = Sort.Direction.ASC)
            }) Pageable pageable, HttpServletRequest request){
        search = getSearchDecode(search);
        return ResponseEntity.ok().body(customDataService.getAllCustomGift(search, pageable));
    }
    @PostMapping("/gift")
    public ResponseEntity<?> createCustomGift(@RequestBody CustomGift request) {
        return customDataService.createCustomGift(request);
    }
    @PutMapping("/gift/{id}")
    public ResponseEntity<?> updateCustomGift(@PathVariable String id,@RequestBody CustomGift request) {
        return customDataService.updateCustomGift(request);
    }
    @DeleteMapping("/gift/{id}")
    public ResponseEntity<?> deleteCustomGift(@PathVariable String id) {
        return customDataService.deleteCustomGift(id);
    }
    // endregion custom gift
}
