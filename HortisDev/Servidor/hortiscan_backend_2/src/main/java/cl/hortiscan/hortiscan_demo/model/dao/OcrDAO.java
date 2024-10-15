package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Ocr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcrDAO extends JpaRepository<Ocr, Integer> {
}
