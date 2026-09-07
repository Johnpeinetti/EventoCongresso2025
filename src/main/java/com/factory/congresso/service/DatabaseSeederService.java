package com.factory.congresso.service;

import com.factory.congresso.model.*;
import com.factory.congresso.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DatabaseSeederService implements CommandLineRunner {

    private final StakeholderTypeRepository stakeholderRepo;
    private final RegionRepository regionRepo;
    private final AcquisitionChannelRepository channelRepo;
    private final TouchpointCatalogRepository catalogRepo;
    private final ParticipantRepository participantRepo;
    private final ParticipantTouchpointRepository touchpointRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        File file = new File("data/Dataset Evento Congresso 2025.xlsx");
        if (!file.exists()) {
            file = new File("Dataset Evento Congresso 2025.xlsx");
        }
        if (!file.exists()) {
            System.err.println("File Excel non trovato! Skip importazione.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // 1. Parsing Dizionario (Foglio 01)
            Sheet sheetDict = workbook.getSheetAt(0);
            Map<String, TouchpointCatalog> catalogMap = new HashMap<>();

            for (int i = 1; i <= sheetDict.getLastRowNum(); i++) {
                Row row = sheetDict.getRow(i);
                if (row == null) continue;

                String header = getCellValueAsString(row.getCell(1));
                String code = getCellValueAsString(row.getCell(2));
                String dataType = getCellValueAsString(row.getCell(3));
                String phase = getCellValueAsString(row.getCell(4));
                String desc = getCellValueAsString(row.getCell(5));

                TouchpointCatalog catalog = TouchpointCatalog.builder()
                        .codeName(code)
                        .sheetHeader(header)
                        .dataType(dataType)
                        .journeyPhase(phase)
                        .description(desc)
                        .build();

                catalogRepo.save(catalog);
                catalogMap.put(header, catalog);
            }

            // 2. Parsing Partecipanti (Foglio 02)
            Sheet sheetPart = workbook.getSheetAt(1);
            Row headerRow = sheetPart.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell).trim());
            }

            for (int i = 1; i <= sheetPart.getLastRowNum(); i++) {
                Row row = sheetPart.getRow(i);
                if (row == null) continue;

                Integer id = (int) row.getCell(0).getNumericCellValue();
                String name = getCellValueAsString(row.getCell(1));
                String email = getCellValueAsString(row.getCell(2)).toLowerCase();
                String stName = getCellValueAsString(row.getCell(3));
                String regName = getCellValueAsString(row.getCell(4));
                String chName = getCellValueAsString(row.getCell(5));
                Boolean inDemDb = row.getCell(6).getNumericCellValue() == 1.0;

                StakeholderType st = stakeholderRepo.findByName(stName)
                        .orElseGet(() -> stakeholderRepo.save(StakeholderType.builder().name(stName).build()));

                Region reg = regionRepo.findByName(regName)
                        .orElseGet(() -> regionRepo.save(Region.builder().name(regName).build()));

                AcquisitionChannel ch = channelRepo.findByName(chName)
                        .orElseGet(() -> channelRepo.save(AcquisitionChannel.builder().name(chName).build()));

                Participant participant = Participant.builder()
                        .originalId(id)
                        .fullName(name)
                        .email(email)
                        .stakeholderType(st)
                        .region(reg)
                        .channel(ch)
                        .inDemDb(inDemDb)
                        .build();

                participantRepo.save(participant);

                // Touchpoints
                for (int j = 7; j < headers.size(); j++) {
                    String h = headers.get(j);
                    Cell cell = row.getCell(j);
                    TouchpointCatalog cat = catalogMap.get(h);

                    if (cat != null && cell != null && cell.getCellType() != CellType.BLANK) {
                        ParticipantTouchpoint pt = ParticipantTouchpoint.builder()
                                .participant(participant)
                                .touchpointCode(cat.getCodeName())
                                .build();

                        switch (cat.getDataType()) {
                            case "booleano" -> pt.setBoolValue(cell.getNumericCellValue() == 1.0);
                            case "conteggio", "minuti" -> pt.setIntValue((int) cell.getNumericCellValue());
                            case "tasso da 0 a 1" -> pt.setFloatValue(cell.getNumericCellValue());
                            case "data" -> {
                                if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                    pt.setDateValue(cell.getLocalDateTimeCellValue().toLocalDate());
                                } else {
                                    String dStr = getCellValueAsString(cell);
                                    if (!dStr.isBlank()) {
                                        pt.setDateValue(parseDate(dStr));
                                    }
                                }
                            }
                        }
                        touchpointRepo.save(pt);
                    }
                }
            }
            System.out.println(">>> IMPORTAZIONE DATASET EXCEL COMPLETATA CON SUCCESSO! <<<");
        }
    }

    private LocalDate parseDate(String dateStr) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}