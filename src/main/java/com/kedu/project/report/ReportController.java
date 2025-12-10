package com.kedu.project.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/report")
@RestController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("/boardSeq")
    public ResponseEntity<Integer> reportBoard(@RequestBody ReportDTO dto) {
        return ResponseEntity.ok(reportService.reportBoard(dto));
    }

    @PostMapping("/commentSeq")
    public ResponseEntity<Integer> reportComment(@RequestBody ReportDTO dto) {
        return ResponseEntity.ok(reportService.reportComment(dto));
    }
}
