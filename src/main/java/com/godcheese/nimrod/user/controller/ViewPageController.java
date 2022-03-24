package com.godcheese.nimrod.user.controller;

import com.godcheese.nimrod.common.others.Common;
import com.godcheese.nimrod.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.godcheese.nimrod.common.security.SimpleUserDetailsServiceImpl.SYSTEM_ADMIN;

/**
 * @author godcheese [godcheese@outlook.com]
 * @date 2018-02-22
 */
@Controller
@RequestMapping(User.Page.VIEW_PAGE)
public class ViewPageController {
    @PreAuthorize("hasRole('" + SYSTEM_ADMIN + "') OR hasAuthority('/SYSTEM/VIEW_PAGE/LIST')")
    @RequestMapping("/list")
    public String list() {
        return Common.trimSlash(User.Page.VIEW_PAGE + "/list");
    }

    @RequestMapping("/add_dialog")
    public String addDialog() {
        return Common.trimSlash(User.Page.VIEW_PAGE + "/add_dialog");
    }

    @RequestMapping("/edit_dialog")
    public String editDialog() {
        return Common.trimSlash(User.Page.VIEW_PAGE + "/edit_dialog");
    }
}
