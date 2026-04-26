package io.student.rcc.api;

import org.springframework.dao.DataIntegrityViolationException;

public interface UserApi {
//    @PostMapping(value = "/register")
//    public String registerUser(@Valid @ModelAttribute RegistrationForm registrationForm,
//                               Errors errors,
//                               Model model,
//                               HttpServletResponse response) {
//        if (!errors.hasErrors()) {
//            final String registeredUserName;
//            try {
//                registeredUserName = userService.registerUser(
//                        registrationForm.username(),
//                        registrationForm.password()
//                );
//                response.setStatus(HttpServletResponse.SC_CREATED);
//                model.addAttribute(MODEL_USERNAME_ATTR, registeredUserName);
//            } catch (DataIntegrityViolationException e) {
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                addErrorToRegistrationModel(
//                        registrationForm,
//                        model,
//                        "username", String.format("Username `%s` already exists", registrationForm.username())
//                );
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        }
//        model.addAttribute(MODEL_FRONT_URI_ATTR, frontUri);
//        return REGISTRATION_VIEW_NAME;
//    }

}
