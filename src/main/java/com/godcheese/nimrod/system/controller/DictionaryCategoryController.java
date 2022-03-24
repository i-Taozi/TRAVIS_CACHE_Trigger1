package com.godcheese.nimrod.system.controller;

import com.godcheese.nimrod.common.others.Common;
import com.godcheese.nimrod.system.System;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author godcheese [godcheese@outlook.com]
 * @date 2018-02-22
 */
@Controller
@RequestMapping(System.Page.DICTIONARY_CATEGORY)
public class DictionaryCategoryController {
    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/add_dialog")
    public String addDialog() {
        return Common.trimSlash(System.Page.DICTIONARY_CATEGORY + "/add_dialog");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/edit_dialog")
    public String editDialog() {
        return Common.trimSlash(System.Page.DICTIONARY_CATEGORY + "/edit_dialog");
    }
}