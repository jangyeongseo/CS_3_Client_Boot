package com.kedu.project.report;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDAO {
    @Autowired
    private SqlSession mybatis;

    public int reportBoard(ReportDTO dto) {
        return mybatis.insert("report.reportBoard", dto);
    }

    public int reportComment(ReportDTO dto) {
        return mybatis.insert("report.reportComment", dto);
    }

    public int admindeleteBoard(int board_seq){
        return mybatis.delete("report.admindeleteBoard", board_seq);
    }

    public int admindeleteCommnet(int comment_seq){
        return mybatis.delete("report.admindeleteCommnet", comment_seq);
    }

}