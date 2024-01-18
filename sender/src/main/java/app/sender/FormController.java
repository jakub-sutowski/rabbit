package app.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/form")
public class FormController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    public String showContactForm(Model model) {
        model.addAttribute("form", new FormRequest());
        return "contact-form";
    }

    @PostMapping
    public String submitContactForm(FormRequest request) {
        rabbitTemplate.convertAndSend("formQueue", request);
        return "redirect:/form/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }
}
