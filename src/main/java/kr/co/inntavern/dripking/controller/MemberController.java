package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.AuthenticationForm;
import kr.co.inntavern.dripking.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/api/member/signup")
    public String signUp(AuthenticationForm authenticationForm, BindingResult bindingResult){
        if(!authenticationForm.getAuthentication_pw().equals(authenticationForm.getAuthentication_pw_check())){
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "";
        }

        memberService.createMember(authenticationForm.getAuthentication_email(),
                authenticationForm.getAuthentication_pw(),
                authenticationForm.getName());
        return "";
    }
}
