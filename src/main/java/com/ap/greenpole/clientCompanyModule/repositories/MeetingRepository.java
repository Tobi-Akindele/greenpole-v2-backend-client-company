package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findAllByClientCompanyId(long clientCompanyId);

}
