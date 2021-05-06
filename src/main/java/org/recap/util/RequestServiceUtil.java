package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.reports.TransactionReport;
import org.recap.model.reports.TransactionReports;
import org.recap.model.search.RequestForm;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ImsLocationDetailRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by rajeshbabuk on 29/10/16.
 */
@Service
public class RequestServiceUtil {

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    ImsLocationDetailRepository imsLocationDetailRepository;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    /**
     * Based on the given search criteria in the request search UI page, this method builds the request search results to show them as rows in the request search UI page.
     *
     * @param requestForm the request form
     * @return the page
     */
    public Page<RequestItemEntity> searchRequests(RequestForm requestForm) {
        String patronBarcode = StringUtils.isNotBlank(requestForm.getPatronBarcode()) ? requestForm.getPatronBarcode().trim() : requestForm.getPatronBarcode();
        String itemBarcode = StringUtils.isNotBlank(requestForm.getItemBarcode()) ? requestForm.getItemBarcode().trim() : requestForm.getItemBarcode();
        String status = StringUtils.isNotBlank(requestForm.getStatus()) ? requestForm.getStatus().trim() : requestForm.getStatus();
        String institution = StringUtils.isNotBlank(requestForm.getInstitution()) ? requestForm.getInstitution().trim() : requestForm.getInstitution();
        InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(institution);
        Integer imsLocationId = imsLocationDetailRepository.findByImsLocationCode(requestForm.getStorageLocation()).getId();
        Optional<InstitutionEntity> institutionEntityOptional = Optional.ofNullable(institutionEntity);
        if (!institutionEntityOptional.isPresent()) {
            institutionEntity = new InstitutionEntity();
            institutionEntity.setId(0);
        }
        Pageable pageable = PageRequest.of(requestForm.getPageNumber(), requestForm.getPageSize(), Sort.Direction.DESC, "id");
        Page<RequestItemEntity> requestItemEntities = null;
        requestItemEntities = (status.equals(RecapConstants.SEARCH_REQUEST_ACTIVE)) ? requestItemDetailsRepository.findByPatronBarcodeAndItemBarcodeAndActiveAndInstitution(pageable, patronBarcode, itemBarcode,imsLocationId, institutionEntity.getId()) : requestItemDetailsRepository.findByPatronBarcodeAndItemBarcodeAndStatusAndInstitution(pageable, patronBarcode, itemBarcode, status,imsLocationId, institutionEntity.getId());

        return requestItemEntities;
    }

    /**
     * @return page requestItemEntities
     */
    public List<RequestItemEntity> exportExceptionReports(String institutionCode, Date fromDate, Date toDate) {
        InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(institutionCode);
        List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByStatusAndInstitutionAndAll(RecapConstants.REPORTS_EXCEPTION, institutionEntity.getId(), fromDate, toDate);
        return requestItemEntities;
    }

    /**
     * @return page requestItemEntities
     */
    public Page<RequestItemEntity> exportExceptionReportsWithDate(String institutionCode, Date fromDate, Date toDate, Integer pageNumber, Integer size) throws ParseException {
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.Direction.DESC, RecapConstants.ID);
        InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(institutionCode);
        Page<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByStatusAndInstitutionAndDateRange(pageable, RecapConstants.REPORTS_EXCEPTION, institutionEntity.getId(), fromDate, toDate);
        return requestItemEntities;
    }
    /**
     *
     * @param fromDate
     * @param toDate
     * @return list of Transaction Reports
     */
    public List<TransactionReport> getTransactionReportCount(TransactionReports transactionReports, Date fromDate, Date toDate) {
        List<TransactionReport> transactionReportsList = new ArrayList<>();
        Map<Integer, String> institutionList = mappingInstitution();
        List<Object[]> list = requestItemDetailsRepository.pullTransactionReportCount(transactionReports.getOwningInsts(),transactionReports.getRequestingInsts(),transactionReports.getTypeOfUses(), fromDate, toDate);
        for (Object[] o : list) {
            transactionReportsList.add(new TransactionReport(o[0].toString(), institutionList.get(Integer.parseInt(o[1].toString())), institutionList.get(Integer.parseInt(o[2].toString())), o[3].toString(), Long.parseLong(o[4].toString())));
        }
        return transactionReportsList;
    }

    /**
     *
     * @param fromDate
     * @param toDate
     * @return list of Transaction Reports
     */
    public List<TransactionReport> getTransactionReports(TransactionReports transactionReports,Date fromDate, Date toDate) {
        List<TransactionReport> transactionReportsList = new ArrayList<>();
        Pageable pageable = PageRequest.of(transactionReports.getPageNumber(), transactionReports.getPageSize());
        Map<Integer, String> institutionList = mappingInstitution();
        List<String> cgdList = (transactionReports.getCgdType().size() > 0) ? transactionReports.getCgdType() : pullCGDList();
        List<Object[]> reportsList = requestItemDetailsRepository.findTransactionReportsByOwnAndReqInstWithStatus(pageable,transactionReports.getOwningInsts(),transactionReports.getRequestingInsts(),transactionReports.getTypeOfUses(), fromDate, toDate, cgdList);
        for (Object[] o : reportsList) {
            transactionReportsList.add(new TransactionReport(o[0].toString(), institutionList.get(Integer.parseInt(o[1].toString())), institutionList.get(Integer.parseInt(o[2].toString())), o[3].toString(), o[4].toString(), o[5].toString(), o[6].toString()));
        }
        return transactionReportsList;
    }

    /**
     *
     * @param transactionReports
     * @param fromDate
     * @param toDate
     * @return th elist of Transaction Reports
     */
    public List<TransactionReport> getTransactionReportsExport(TransactionReports transactionReports,Date fromDate, Date toDate) {
        List<TransactionReport> transactionReportsList = new ArrayList<>();
        Map<Integer, String> institutionList = mappingInstitution();
        List<String> cgdList = (transactionReports.getCgdType().size() > 0) ? transactionReports.getCgdType() : pullCGDList();
        List<Object[]> reportsList = requestItemDetailsRepository.findTransactionReportsByOwnAndReqInstWithStatusExport(transactionReports.getOwningInsts(),transactionReports.getRequestingInsts(),transactionReports.getTypeOfUses(), fromDate, toDate, cgdList);
        for (Object[] o : reportsList) {
            transactionReportsList.add(new TransactionReport(o[0].toString(), institutionList.get(Integer.parseInt(o[1].toString())), institutionList.get(Integer.parseInt(o[2].toString())), o[3].toString(), o[4].toString(), o[5].toString(), o[6].toString()));
        }
        return transactionReportsList;
    }
    private Map<Integer, String> mappingInstitution() {
        Map<Integer, String> institutionList = new HashMap<>();
        List<InstitutionEntity> institutionEntities = institutionDetailsRepository.getInstitutionCodes();
        institutionEntities.stream().forEach(inst -> institutionList.put(inst.getId(), inst.getInstitutionCode()));
        return institutionList;
    }

    private List<String> pullCGDList() {
        List<String> cgdList = new ArrayList<>();
        List<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
        collectionGroupEntities.stream().forEach(cgd -> cgdList.add(cgd.getCollectionGroupCode()));
        return cgdList;
    }
}
