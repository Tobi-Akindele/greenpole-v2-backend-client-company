package com.ap.greenpole.clientCompanyModule.controllers.util;

import com.ap.greenpole.clientCompanyModule.dtos.*;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.ap.greenpole.clientCompanyModule.utils.Const;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * Created by Nelu on 18/08/2020.
 */

public class EntityMapper {

    private static Map<Integer,String>StatusMap(){

        Map<Integer, String> map = new HashMap<>();
        map.put(1,"PENDING");
        map.put(2,"REJECTED");
        map.put(3,"APPROVED");

        return map;
    }

    public static ModuleResponseDto mapToModuleResponseDto(ModuleRequest moduleRequest){
        if (moduleRequest == null) return null;

        return ModuleResponseDto.builder()
                .createdOn(moduleRequest.getCreatedOn())
                .requesterId(moduleRequest.getRequesterId())
                .requestId(moduleRequest.getRequestId())
                .status(StatusMap().get(moduleRequest.getStatus()))
                .build();

    }

    public static CreateClientCompanyResponseDto mapModuleRequestToCreateClientCompanyResponse(ModuleResponseDto response, ClientCompanyRequestDto clientCompanyRequestDto){

        CreateClientCompanyResponseDto dto = new CreateClientCompanyResponseDto();
        dto.setResponseDto(response);
        dto.setDto(clientCompanyRequestDto);

        return dto;
    }

        public static ClientCompanyResponseDto mapToClientCompanyResponseDto(ClientCompany clientCompany){

        if (clientCompany == null) return null;

        ClientCompanyResponseDto dto = new ClientCompanyResponseDto();
        dto.setId(clientCompany.getId());
        dto.setStatus(clientCompany.getStatus());
        dto.setAddress(clientCompany.getAddress());
        dto.setCeoName(clientCompany.getCeoName());
        dto.setChairmanName(clientCompany.getChairmanName());
        dto.setDateOfIncorporation(clientCompany.getDateOfIncorporation());
        dto.setDepository(clientCompany.getDepository());
        dto.setExchange(clientCompany.getExchange());
        dto.setNseSector(clientCompany.getNseSector());
        dto.setRegisterCode(clientCompany.getRegisterCode());
        dto.setRegisterName(clientCompany.getRegisterName());
        dto.setSymbol(clientCompany.getSymbol());
        dto.setCountry(clientCompany.getCountry());
        dto.setLga(clientCompany.getLga());
        dto.setRcNumber(clientCompany.getRcNumber());
        dto.setState(clientCompany.getState());
        dto.setPostalCode(clientCompany.getPostalCode());
        dto.setAlternatePhoneNumber(clientCompany.getAlternatePhoneNumber());
        dto.setPhoneNumber(clientCompany.getPhoneNumber());
        dto.setDirectors(mapDirectorToDtos(clientCompany.getDirectors()));
        dto.setEmailAddress(clientCompany.getEmailAddress());
        dto.setRegistrationCode(clientCompany.getRegistrationCode());
        dto.setSecretary(clientCompany.getSecretary());
        dto.setAuthorizedShareCapital(clientCompany.getAuthorizedShareCapital());
        dto.setPaidUpShareCapital(clientCompany.getPaidUpShareCapital());

        return dto;
    }

    public static List<ClientCompanyResponseDto> mapToClientCompanyResponseDtos(
            List<ClientCompany> clientCompanies) {

        List<ClientCompanyResponseDto> dtos = new ArrayList<>();
        clientCompanies.forEach(
                clientCompany -> dtos.add(mapToClientCompanyResponseDto(clientCompany)));
        return dtos;
    }

    public static ModuleRequest mapToClientCompanyApprovalRequest(
            ClientCompanyRequestDto dto) {

        if (dto == null) return null;

        Gson gson = new GsonBuilder().setDateFormat(Const.DATE_FORMATE).create();

        return ModuleRequest.builder()
                .actionRequired(Const.CREATE)
                .status(Const.PENDING)
                .createdOn(new Date())
                .modules(ConstantUtils.MODULE)
                .newRecord(gson.toJson(dto))
                .build();
    }

    public static ModuleRequest mapToManualShareCapitalApprovalRequest(ManualShareCapitalDto dto, long clientCompanyId){

        if (dto == null) return null;

        Gson gson = new GsonBuilder().setDateFormat(Const.DATE_FORMATE).create();

        return ModuleRequest.builder()
                .actionRequired(Const.MANUAL_SHARE_CAPITAL)
                .status(Const.PENDING)
                .resourceId(clientCompanyId)
                .createdOn(new Date())
                .modules(ConstantUtils.MODULE)
                .newRecord(gson.toJson(dto))
                .build();
    }

    public static ModuleRequest mapToTradeRightsRequest(TradeRightsDto tradeRightsDto){

        if (tradeRightsDto == null) return null;

        return ModuleRequest.builder()
                .actionRequired(Const.TRADE_RIGHTS)
                .status(Const.PENDING)
                .createdOn(new Date())
                .modules(Const.SHAREHOLDER)
                .newRecord(new Gson().toJson(tradeRightsDto))
                .build();

    }

    public static MappedModuleResponseDto mappedRequest(ModuleRequest request){

        if (request == null) return null;

        return MappedModuleResponseDto.builder()
                .actionRequired(request.getActionRequired())
                .createdOn(request.getCreatedOn())
                .modules(request.getModules())
                .requestCode(request.getRequestCode())
                .actionRequired(request.getActionRequired())
                .createdOn(request.getCreatedOn())
                .requesterId(request.getRequesterId())
                .resourceId(request.getResourceId())
                .requestId(request.getRequestId())
                .status(StatusMap().get(request.getStatus()))
                .build();
    }


    public static ModuleRequest mapToClientCompanyApprovalRequest(List<ShareholderIntroductionDto> shareholderIntroductionDtos, long clientCompanyId){

        if (shareholderIntroductionDtos == null) return null;

        Gson gson = new GsonBuilder().setDateFormat(Const.DATE_FORMATE).create();

        return ModuleRequest.builder()
                .actionRequired(Const.CREATE_SHAREHOLDER_INTRODUCTION)
                .status(Const.PENDING)
                .createdOn(new Date())
                .resourceId(clientCompanyId)
                .modules(Const.SHAREHOLDER)
                .newRecord(gson.toJson(shareholderIntroductionDtos))
                .build();
    }

    public static ModuleRequest mapInMassToClientCompanyApprovalRequest(List<ShareHolderInMassRequestDto> shareHolderInMassRequestDtos, long clientCompanyId){

        if (shareHolderInMassRequestDtos == null) return null;

        return ModuleRequest.builder()
                .actionRequired(Const.CREATE_SHAREHOLDER_IN_MASS)
                .status(Const.PENDING)
                .createdOn(new Date())
                .resourceId(clientCompanyId)
                .modules(Const.SHAREHOLDER)
                .newRecord(new Gson().toJson(shareHolderInMassRequestDtos))
                .build();
    }

    public static Shareholder mapIntroductionRequestToShareHolder(ShareholderIntroductionDto dto){

        if (dto == null) return null;

        Shareholder shareholder = new Shareholder();
        shareholder.setStatus(GenericStatusEnum.ACTIVE);
        shareholder.setAddress(dto.getAddress());
        shareholder.setShareUnit(dto.getShareUnit());
        shareholder.setNuban(dto.getNuban());
        shareholder.setClearingHousingNumber(dto.getClearingHousingNumber());
        shareholder.setCreatedOn(new Date());
        shareholder.setPhone(dto.getPhone());
        shareholder.setFirstName(dto.getFirstName());
        shareholder.setMiddleName(dto.getMiddleName());
        shareholder.setLastName(dto.getLastName());
        shareholder.setEmail(dto.getEmail());
        shareholder.setPostalCode(dto.getPostalCode());
        shareholder.setShareholderType(dto.getShareholderType());
        shareholder.setBvn(dto.getBvn());
        shareholder.setBankName(dto.getBankName());
        shareholder.setEsopStatus(dto.getEsopStatus());
        shareholder.setRegistrarMandated(dto.getRegisterMandated());
        shareholder.setTaxExemption(dto.getTaxExemption());
        shareholder.setShareUnit(dto.getShareUnit());
        shareholder.setShareholderType(dto.getShareholderType());


        return shareholder;
    }


    public static Shareholder mapInMassRequestToShareHolder(ShareHolderInMassRequestDto shareHolderInMassRequestDto){

        if (shareHolderInMassRequestDto == null) return null;

        Shareholder shareholder = new Shareholder();
        shareholder.setStatus(GenericStatusEnum.ACTIVE);
        shareholder.setAddress(shareHolderInMassRequestDto.getAddress());
        shareholder.setShareUnit(shareHolderInMassRequestDto.getShareUnit());
        shareholder.setNuban(shareHolderInMassRequestDto.getNuban());
        shareholder.setClearingHousingNumber(shareHolderInMassRequestDto.getClearingHouseNumber());
        shareholder.setCreatedOn(new Date());
        shareholder.setCity(shareHolderInMassRequestDto.getCity());
        shareholder.setPhone(shareHolderInMassRequestDto.getPhoneNumber());
        shareholder.setKinName(shareHolderInMassRequestDto.getKinName());
        shareholder.setKinAddress(shareHolderInMassRequestDto.getKinAddress());
        shareholder.setKinPhone(shareHolderInMassRequestDto.getKinPhone());
        shareholder.setKinEmail(shareHolderInMassRequestDto.getKinEmail());
        shareholder.setFirstName(shareHolderInMassRequestDto.getFirstName());
        shareholder.setMiddleName(shareHolderInMassRequestDto.getMiddleName());
        shareholder.setLastName(shareHolderInMassRequestDto.getLastName());
        shareholder.setEmail(shareHolderInMassRequestDto.getEmail());
        shareholder.setPostalCode(shareHolderInMassRequestDto.getPostalCode());
        shareholder.setShareholderType(shareHolderInMassRequestDto.getShareholderType());
        shareholder.setBvn(shareHolderInMassRequestDto.getBvn());
        shareholder.setBankName(shareHolderInMassRequestDto.getBankName());
        shareholder.setEsopStatus(shareHolderInMassRequestDto.getEsopStatus());


        return shareholder;

    }

    public static CircularRightsDto mapShareHolderToCircularRightsDto(Shareholder shareholder){

        if (shareholder == null) return null;

        CircularRightsDto circularRightsDto = new CircularRightsDto();
        circularRightsDto.setAccount(shareholder.getBankAccount());
        circularRightsDto.setAddress(shareholder.getAddress());
        circularRightsDto.setCity(shareholder.getCity());
        circularRightsDto.setFirstName(shareholder.getFirstName());
        circularRightsDto.setGender(shareholder.getGender());
        circularRightsDto.setKinPhone(shareholder.getKinPhone());
        circularRightsDto.setLgaOfOrigin(shareholder.getLgaOfOrigin());
        circularRightsDto.setEmail(shareholder.getEmail());
        circularRightsDto.setNuban(shareholder.getNuban());
        circularRightsDto.setMaritalStatus(shareholder.getMaritalStatus());
        circularRightsDto.setShareUnits(shareholder.getShareUnit());
        circularRightsDto.setStateOfOrigin(shareholder.getStateOfOrigin());
        circularRightsDto.setMiddleName(shareholder.getMiddleName());
        circularRightsDto.setLastName(shareholder.getLastName());
        circularRightsDto.setKinName(shareholder.getKinName());
        circularRightsDto.setKinAddress(shareholder.getKinAddress());
        circularRightsDto.setOccupation(shareholder.getOccupation());
        circularRightsDto.setPostalCode(shareholder.getPostalCode());
        circularRightsDto.setKinEmail(shareholder.getKinEmail());
        circularRightsDto.setPhoneNumber(shareholder.getPhone());

        return circularRightsDto;
    }

    public static List<CircularRightsDto> mapShareHoldersToCircularRightsDtos(List<Shareholder> shareholders) {
        List<CircularRightsDto> circularRightsDtos = new ArrayList<>();
        shareholders.forEach(shareholder -> circularRightsDtos.add(mapShareHolderToCircularRightsDto(shareholder)));
        return circularRightsDtos;
    }

    public static ShareHolderInMassResponseDto mapToShareHolderToResponseDto(Shareholder shareholder){

        if (shareholder == null) return null;

        ShareHolderInMassResponseDto responseDto = new ShareHolderInMassResponseDto();
        responseDto.setShareHolderId(shareholder.getShareholder_id());
        responseDto.setAddress(shareholder.getAddress());
        responseDto.setPhoneNumber(shareholder.getPhone());
        responseDto.setCity(shareholder.getCity());
        responseDto.setClearingHouseNumber(shareholder.getClearingHousingNumber());
        responseDto.setCreatedDate(shareholder.getCreatedOn());
        responseDto.setEmail(shareholder.getEmail());
        responseDto.setFirstName(shareholder.getFirstName());
        responseDto.setMiddleName(shareholder.getMiddleName());
        responseDto.setLastName(shareholder.getLastName());
        responseDto.setKinAddress(shareholder.getKinAddress());
        responseDto.setKinEmail(shareholder.getKinEmail());
        responseDto.setKinName(shareholder.getKinName());
        responseDto.setKinPhone(shareholder.getKinPhone());
        responseDto.setKinAddress(shareholder.getKinAddress());
        responseDto.setNuban(shareholder.getNuban());
        responseDto.setShareUnit(shareholder.getShareUnit());
        responseDto.setStatus(shareholder.getStatus());

        return responseDto;
    }

    public static List<ShareHolderInMassResponseDto> mapToShareHolderToResponseDtos(List<Shareholder> shareholders) {
        List<ShareHolderInMassResponseDto> shareHolderInMassResponseDtos = new ArrayList<>();
        shareholders.forEach(shareholder -> shareHolderInMassResponseDtos.add(mapToShareHolderToResponseDto(shareholder)));
        return shareHolderInMassResponseDtos;
    }


    public static List<Shareholder> mapInMassRequestToShareHolders(List<ShareHolderInMassRequestDto> shareHolderInMassRequestDtos) {
        List<Shareholder> shareholders = new ArrayList<>();
        shareHolderInMassRequestDtos.forEach(shareHolderInMassRequestDto -> shareholders.add(mapInMassRequestToShareHolder(shareHolderInMassRequestDto)));
        return shareholders;
    }

    public static ModuleRequest mapToInvalidateRequest(ClientCompany clientCompany){

        if (clientCompany == null) return null;

        Gson gson = new GsonBuilder().setDateFormat(Const.DATE_FORMATE).create();

        return ModuleRequest.builder()
                .actionRequired(Const.INVALIDATE)
                .status(Const.PENDING)
                .createdOn(new Date())
                .resourceId(clientCompany.getId())
                .modules(ConstantUtils.MODULE)
                .oldRecord(gson.toJson(mapToClientCompanyResponseDto(clientCompany)))
                .build();
    }


    public static DirectorDto mapDirectorToDto(Director director){

        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setEmail(director.getEmail());
        dto.setFirstName(director.getFirstName());
        dto.setLastName(director.getLastName());
        dto.setMiddleName(director.getMiddleName());

        return dto;
    }

    public static List<DirectorDto> mapDirectorToDtos(List<Director> directors){

        List<DirectorDto> dtos = new ArrayList<>();
        directors.forEach(director -> dtos.add(mapDirectorToDto(director)));
        return dtos;
    }

    public static Director mapDtoToDirector(DirectorDto directorDto) {

        Director director = new Director();

        director.setFirstName(directorDto.getFirstName());
        director.setMiddleName(directorDto.getMiddleName());
        director.setLastName(directorDto.getLastName());
        director.setEmail(directorDto.getEmail());
        director.setPhoneNumber(directorDto.getPhoneNumber());

        return director;
    }

    public static List<Director> mapDtosToDirectors(List<DirectorDto> directorDtos){

        List<Director> directors = new ArrayList<>();
        directorDtos.forEach(directorDto -> directors.add(mapDtoToDirector(directorDto)));
        return directors;
    }

    public static Shareholder mapDtoToShareholder(ShareHolderRequestDto shareHolderRequestDto){

        return Shareholder.builder()
                .shareUnit(shareHolderRequestDto.getShareUnit())
                .address(shareHolderRequestDto.getAddress())
                .firstName(shareHolderRequestDto.getFirstName())
                .middleName(shareHolderRequestDto.getMiddleName())
                .lastName(shareHolderRequestDto.getLastName())
                .address(shareHolderRequestDto.getAddress())
                .bankAccount(shareHolderRequestDto.getBankAccount())
                .bvn(shareHolderRequestDto.getBvn())
                .clearingHousingNumber(shareHolderRequestDto.getClearingHousingNumber())
                .phone(shareHolderRequestDto.getPhone())
                .email(shareHolderRequestDto.getEmail())
                .build();
    }

    public static List<Shareholder> mapDtosToShareholders(List<ShareHolderRequestDto> shareHolderRequestDtos){
        List<Shareholder> shareholders = new ArrayList<>();
        shareHolderRequestDtos.forEach(shareHolderDto -> shareholders.add(mapDtoToShareholder(shareHolderDto)));
        return shareholders;
    }

    public static ShareHolderResponseDto mapShareholderToResponseDto(Shareholder shareholder){

        ShareHolderResponseDto dto = new ShareHolderResponseDto();
        dto.setShareHolderId(shareholder.getShareholder_id());
        dto.setFirstName(shareholder.getFirstName());
        dto.setMiddleName(shareholder.getMiddleName());
        dto.setLastName(shareholder.getLastName());
        dto.setEmail(shareholder.getEmail());
        dto.setStatus(shareholder.getStatus());
        dto.setAccount(shareholder.getBankAccount());
        dto.setHolding(shareholder.getShareUnit());
        dto.setAddress(shareholder.getAddress());

        return dto;
    }

    public static List<ShareHolderResponseDto> mapShareholderToResponseDtos(List<Shareholder> shareholders){
        List<ShareHolderResponseDto> dtos = new ArrayList<>();
        shareholders.forEach(shareholder -> dtos.add(mapShareholderToResponseDto(shareholder)));
        return dtos;
    }

    public static Meeting mapDtoToMeeting(MeetingRequestDto meetingRequestDto){

        if (meetingRequestDto == null) return null;

        Meeting meeting = new Meeting();
        meeting.setTitle(meetingRequestDto.getTitle());
        meeting.setPurpose(meetingRequestDto.getPurpose());
        meeting.setStartDate(meetingRequestDto.getStartDate());
        meeting.setEndDate(meetingRequestDto.getEndDate());
        meeting.setOtherNotes(meetingRequestDto.getOtherNotes());
        meeting.setMeetingType(meetingRequestDto.getMeetingType());

        return meeting;
    }

    public static MeetingResponseDto mapMeetingToResponseDto(Meeting meeting){

        if (meeting == null) return null;

        return MeetingResponseDto.builder()
                .endDate(meeting.getEndDate())
                .otherNotes(meeting.getOtherNotes())
                .startDate(meeting.getStartDate())
                .meetingType(meeting.getMeetingType())
                .title(meeting.getTitle())
                .purpose(meeting.getPurpose())
                .id(meeting.getId())
                .dateCreated(meeting.getDateCreated())
                .build();
    }

    public static List<MeetingResponseDto> mapMeetingsToDtos(List<Meeting> meetings){
        List<MeetingResponseDto> dtos = new ArrayList<>();
        meetings.forEach(meeting -> dtos.add(mapMeetingToResponseDto(meeting)));
        return dtos;
    }



    public static AttendanceResponseDto mapMeetingToAttendanceResponse(Meeting meeting, List<Shareholder> attendees){

        return AttendanceResponseDto.builder()
                .meetingId(meeting.getId())
                .attendees(mapShareholderToResponseDtos(attendees))
                .build();
    }

}
