package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchRecordsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rajeshbabuk on 2/1/17.
 */
@Service
public class SearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(SearchUtil.class);

    @Value("${scsb.gateway.url}")
    private String scsbUrl;

    /**
     * This method makes a call to scsb microservice to get search results response for the given search criteria in the search UI page.
     *
     * @param searchRecordsRequest the search records request
     * @return the search records response
     */
    public SearchRecordsResponse requestSearchResults(SearchRecordsRequest searchRecordsRequest) {
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = HelperUtil.getSwaggerHeaders();
            HttpEntity<SearchRecordsRequest> httpEntity = new HttpEntity<>(searchRecordsRequest, headers);

            ResponseEntity<SearchRecordsResponse> responseEntity = restTemplate.exchange(scsbUrl + ScsbConstants.SCSB_SEARCH_SERVICE_URL, HttpMethod.POST, httpEntity, SearchRecordsResponse.class);
            searchRecordsResponse = responseEntity.getBody();
            return searchRecordsResponse;
        } catch (Exception e) {
            logger.error(ScsbCommonConstants.LOG_ERROR, e);
            return searchRecordsResponse;
        }
    }

    public SearchRecordsResponse searchRecord(SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.resetPageNumber();
        return searchAndSetResults(searchRecordsRequest);
    }

    public SearchRecordsResponse searchAndSetResults(SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.reset();
        searchRecordsRequest.setSearchResultRows(null);
        searchRecordsRequest.setShowResults(true);
        searchRecordsRequest.setSelectAll(false);
        SearchRecordsResponse searchRecordsResponse = requestSearchResults(searchRecordsRequest);
        if (CollectionUtils.isEmpty(searchRecordsResponse.getSearchResultRows())) {
            searchRecordsResponse.setTotalRecordsCount(String.valueOf(0));
            searchRecordsResponse.setTotalBibRecordsCount(String.valueOf(0));
            searchRecordsResponse.setTotalItemRecordsCount(String.valueOf(0));
            if (searchRecordsResponse.getErrorMessage() == null) {
                searchRecordsResponse.setErrorMessage(ScsbCommonConstants.SEARCH_RESULT_ERROR_NO_RECORDS_FOUND);
            }
        }
        return searchRecordsResponse;
    }

}
