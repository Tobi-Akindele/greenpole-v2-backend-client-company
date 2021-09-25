package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.ModuleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;

@Repository
public interface ClientCompanyRequestRepository extends PagingAndSortingRepository<ModuleRequest, Long>, JpaSpecificationExecutor<ModuleRequest> {

     @Query(value = "SELECT * FROM tbl_request_approval WHERE modules = ?1", nativeQuery = true)
     Page<ModuleRequest> findAllByModules(Pageable pageable, String module);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE modules = ?1 AND requester_id = ?2", nativeQuery = true)
     Page<ModuleRequest> findAllByModulesAndRequesterId(Pageable pageable, String module, long requesterId);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE status = ?1 AND modules = ?2", nativeQuery = true)
     Page<ModuleRequest> findAllByStatusAndModules(Pageable pageable, int status, String module);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE status = ?1 AND modules = ?2 AND requester_id = ?3", nativeQuery = true)
     Page<ModuleRequest> findAllByStatusAndModulesAndRequesterId(Pageable pageable, int status, String module, long requesterId);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE status = ?1 AND modules = ?2 AND requester_id = ?3", nativeQuery = true)
     Page<ModuleRequest> findAllByStatusAndModulesAAndRequesterId(Pageable pageable, int status, String module, long requesterId);

     ModuleRequest findModuleRequestByRequestId(long id);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE status = ?1", nativeQuery = true)
     List<ModuleRequest> awaitingApproval(int pendingApproval);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE status = ?1", nativeQuery = true)
     List<ModuleRequest> approved(int approved);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE status = ?1", nativeQuery = true)
     List<ModuleRequest> rejected(int rejected);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE resource_id = ?1 || status != ?2 LIMIT 1", nativeQuery = true)
     ModuleRequest checkPending(long id, int approved);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE request_id = ?1 || status = ?2 LIMIT 1", nativeQuery = true)
     ModuleRequest checkRejected(long id, int rejected);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE resource_id = ?1 AND action_required = ?2 AND modules = ?3 ORDER BY request_id DESC LIMIT 1", nativeQuery = true)
     ModuleRequest findFirstModuleRequestByResourceIdAndActionRequiredAndModulesOrderByRequestIdDesc(Long resourceId, String actionRequired, String modules);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE modules = ?1 AND created_on =?2", nativeQuery = true)
      Page<ModuleRequest> findModuleRequestByModulesAndCreatedOn(Pageable pageable, String module, Date date);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE modules = ?1 AND created_on =?2 AND requester_id = ?3", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByModulesAndCreatedOnAndRequesterId(Pageable pageable, String module, Date date, long requesterId);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE modules = ?1 AND created_on BETWEEN ?2 AND ?3 AND status = ?4", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByModulesAndCreatedOnBetweenAndStatus(String module, Date dateObject,Date endDate, int status, Pageable pageable);

     @Query(value = "SELECT * FROM tbl_request_approval WHERE modules = ?1 AND created_on BETWEEN ?2 AND ?3 AND status = ?4 AND requester_id = ?5", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByModulesAndCreatedOnBetweenAndStatusAndRequesterId(String module, Date dateObject,Date endDate, int status, long requesterId, Pageable pageable);

     @Query(value = "SELECT * FROM tbl_request_approval where resource_id = ?1 AND modules = ?2", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByResourceId(Pageable pageable,long resourceId, String modules);

     @Query(value = "SELECT * FROM tbl_request_approval where resource_id = ?1 AND status = ?2 AND modules = ?3", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByResourceIdAndStatus(Pageable pageable, long resourceId, int status, String modules);

     @Query(value = "SELECT * FROM tbl_request_approval where  status = ?1 AND modules = ?2", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByStatus(Pageable pageable,int status, String modules);

     @Query(value = "SELECT * FROM tbl_request_approval where modules = ?1", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByModules(Pageable pageable, String modules);

     @Query(value = "SELECT * FROM tbl_request_approval where lower(old_record) like concat('%', lower(?1), '%') OR lower(new_record) like concat('%', lower(?1), '%') AND modules = ?2", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByNewRecordAndOldRecord(Pageable pageable, String param, String module);

     @Query(value = "SELECT * FROM tbl_request_approval where lower(old_record) like concat('%', lower(?1), '%') OR lower(new_record) like concat('%', lower(?1), '%') AND modules = ?2 AND requester_id = ?3", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByNewRecordAndOldRecordAAndRequesterId(Pageable pageable, String param, String module, long requesterId);

     @Query(value = "SELECT * FROM tbl_request_approval where lower(old_record) like concat('%', lower(?1), '%') OR lower(new_record) like concat('%', lower(?1), '%') AND modules = ?2 AND status = ?3 AND requester_id = ?4", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByNewRecordAndOldRecordAndStatusAndRequesterId(Pageable pageable, String param, String module, int status, long requesterId);

     @Query(value = "SELECT * FROM tbl_request_approval where lower(old_record) like concat('%', lower(?1), '%') OR lower(new_record) like concat('%', lower(?1), '%') AND modules = ?2 AND status = ?3", nativeQuery = true)
     Page<ModuleRequest> findModuleRequestByNewRecordAndOldRecordAndStatus(Pageable pageable, String param, String module, int status);
}
