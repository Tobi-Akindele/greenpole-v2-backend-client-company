//package com.ap.greenpole.clientCompanyModule.repositories;
//
//import com.ap.greenpole.clientCompanyModule.entity.Meeting;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface MeetingAttendeesRepository {
//
//    @Query("SELECT ma.attendees FROM meeting_attendees ma WHERE meeting_id = ?1")
//    List<Long> findAllShareholdersInAttendance(Long meetingId);
//}
