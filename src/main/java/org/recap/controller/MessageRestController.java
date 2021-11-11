package org.recap.controller;

import org.json.JSONObject;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageRestController {
    @Autowired
    PropertyUtil propertyUtil;

    @GetMapping("/ins/{institutionCode}")
    public Map<String, Object> getValue(@PathVariable("institutionCode") String institutionCode) {
        JSONObject json = propertyUtil.getPropertyByInstitution(institutionCode, false);
        return json.toMap();
    }
}