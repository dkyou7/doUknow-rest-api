package com.example.dynrestapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto dto) {
        Event event = modelMapper.map(dto,Event.class);
        Event savedEvent = eventRepository.save(event);
        //EventController의 id에 해당하는 링크를 만들고 링크를 URI로 변환
        URI createdUri = linkTo(EventController.class).slash(savedEvent.getId()).toUri();
        // createdUri 헤더를 가지고 201응답을 만듬
        return ResponseEntity.created(createdUri).body(event);
    }
}