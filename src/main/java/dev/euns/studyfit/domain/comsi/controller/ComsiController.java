package dev.euns.studyfit.domain.comsi.controller;

import dev.euns.studyfit.global.response.BaseResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.request.SchoolSearchRequest;
import dev.euns.studyfit.infrastructure.comsi.dto.response.ClassSelectionResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.PeriodSelectionResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.SchoolSearchResponse;
import dev.euns.studyfit.infrastructure.comsi.dto.response.TimetableResponse;
import dev.euns.studyfit.infrastructure.comsi.service.ComsiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class ComsiController {

    private final ComsiService comsiService;

    @PostMapping("/search")
    public BaseResponse<SchoolSearchResponse> searchSchools(@RequestBody SchoolSearchRequest request) {
        return BaseResponse.success(comsiService.searchSchools(request.getKeyword()));
    }

    @GetMapping("/{code}/classes")
    public BaseResponse<ClassSelectionResponse> getClasses(@PathVariable int code) {
        return BaseResponse.success(comsiService.getClassSelection(code));
    }

    @GetMapping("/{code}/periods")
    public BaseResponse<PeriodSelectionResponse> getPeriods(@PathVariable int code) {
        return BaseResponse.success(comsiService.getPeriodSelection(code));
    }

    @GetMapping("/{code}/timetable")
    public BaseResponse<TimetableResponse> getTimetable(
            @PathVariable int code,
            @RequestParam(defaultValue = "1") int grade,
            @RequestParam(defaultValue = "1") int classNumber,
            @RequestParam(defaultValue = "1") int dateIndex
    ) {
        return BaseResponse.success(comsiService.getTimetable(code, grade, classNumber, dateIndex));
    }
}
