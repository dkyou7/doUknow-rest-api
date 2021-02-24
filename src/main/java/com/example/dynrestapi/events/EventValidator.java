package com.example.dynrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0){
            errors.rejectValue("basePrice","worngValue","BasePrice is wrong");
            errors.rejectValue("maxPrice","worngValue","maxPrice is wrong");
        }
    }
}
