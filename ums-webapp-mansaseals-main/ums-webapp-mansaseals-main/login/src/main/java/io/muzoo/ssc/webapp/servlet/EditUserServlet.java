package io.muzoo.ssc.webapp.servlet;

import io.muzoo.ssc.webapp.Routable;
import io.muzoo.ssc.webapp.model.User;
import io.muzoo.ssc.webapp.service.SecurityService;
import io.muzoo.ssc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditUserServlet extends HttpServlet implements Routable {

    private SecurityService securityService;

    @Override
    public String getMapping() {
        return "/user/edit";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);

        if (authorized) {

            String username= StringUtils.trim(request.getParameter("username"));
            String displayName= StringUtils.trim(request.getParameter("displayName"));


            UserService userService= UserService.getInstance();
            String errorMessage = null;
            User user = userService.findByUsername(username);
            if (user == null){
                errorMessage = String.format("User %s does not exist", username);
            }
            else if (StringUtils.isBlank(displayName)){
                errorMessage = "Display cannot be blank";
            }

            if (errorMessage!=null){
                request.getSession().setAttribute("hasError", true);
                request.getSession().setAttribute("message", errorMessage);
            }
            else {
                try {

                    userService.updateUserByUsername(username,displayName);
                    request.getSession().setAttribute("hasError", false);
                    request.getSession().setAttribute("message", String.format("User %s has been updated successfully.", username));
                    response.sendRedirect("/");
                    return;

                } catch (Exception e){

                    request.getSession().setAttribute("hasError", true);
                    request.getSession().setAttribute("message", e.getMessage());

                }

            }


            request.setAttribute("username", username);
            request.setAttribute("display_name", displayName);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/edit.jsp");
            rd.include(request, response);
            // do MVC in here
//            String username = (String) request.getSession().getAttribute("username");
//            UserService userService = UserService.getInstance();
//
//
//            request.setAttribute("user", userService.findByUsername(username));
//            request.setAttribute("users", userService.findAll());


//            request.getSession().removeAttribute("hasError");
//            request.getSession().removeAttribute("message");



        } else {
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);

        if (authorized) {
            String username= StringUtils.trim(request.getParameter("username"));
            UserService userService= UserService.getInstance();
            // do MVC in here
//            String username = (String) request.getSession().getAttribute("username");
//            UserService userService = UserService.getInstance();
//
//
//            request.setAttribute("user", userService.findByUsername(username));
//            request.setAttribute("users", userService.findAll());

            User user = userService.findByUsername(username);

            request.setAttribute("user", user);
            request.setAttribute("username", user.getUsername());
            request.setAttribute("displayName", user.getDisplayName());
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/edit.jsp");
            rd.include(request, response);


            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");



        } else {
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }
}