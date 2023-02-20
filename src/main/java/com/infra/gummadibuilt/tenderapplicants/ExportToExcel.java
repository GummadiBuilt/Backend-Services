package com.infra.gummadibuilt.tenderapplicants;

import com.fasterxml.jackson.databind.JsonNode;
import com.infra.gummadibuilt.common.file.FileDownloadDto;
import com.infra.gummadibuilt.common.util.CommonModuleUtils;
import com.infra.gummadibuilt.tenderapplicants.model.dto.ApplicantsComparisonDto;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ExportToExcel {

    private final TenderApplicantsService tenderApplicantsService;

    public ExportToExcel(TenderApplicantsService tenderApplicantsService) {
        this.tenderApplicantsService = tenderApplicantsService;
    }

    public FileDownloadDto exportToExcel(String tenderId, List<String> applicantId, HttpServletRequest request) throws IOException {
        List<ApplicantsComparisonDto> comparisonDtos = tenderApplicantsService.compareApplicants(tenderId, applicantId, request);
        File tempFile = File.createTempFile(String.format("temp_%s", tenderId), ".xlsx");
        FileDownloadDto fileDownloadDto = new FileDownloadDto();

        try (FileOutputStream outputStream = new FileOutputStream(tempFile); Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = workbook.createCellStyle();
            // Use for headers, bold text
            XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
            headerFont.setFontName("Calibri");
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setAlignment(HorizontalAlignment.LEFT);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THICK);

            // Cell Font
            XSSFFont cellFont = ((XSSFWorkbook) workbook).createFont();
            cellFont.setColor(IndexedColors.WHITE.getIndex());

            // Use for data
            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setWrapText(true);
            style.setFont(cellFont);
            style.setBorderBottom(BorderStyle.THICK);

            CellStyle colorStyle = workbook.createCellStyle();
            colorStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            colorStyle.setWrapText(true);
            colorStyle.setBorderBottom(BorderStyle.THICK);

            Map<String, CellStyle> styleMap = new HashMap<>();
            styleMap.put("n", style);
            styleMap.put("n+1", colorStyle);

            Sheet sheet = workbook.createSheet(String.format("Tender_%s_comparison", tenderId));
            sheet.setColumnWidth(0, 9000);

            List<String> headerList = Arrays.asList(CommonModuleUtils.excelHeaders());

            headerList.forEach(header -> {

                int index = headerList.indexOf(header);
                Row row = createCell(sheet, index, header, headerStyle);
                switch (header) {
                    case "Company Name":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getCompanyName()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Year of establishment":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getYearOfEstablishment()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Type of establishment":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getTypeOfEstablishment()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Postal Address[Corporate Office]":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getCorpOfficeAddress()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Postal Address[Local Office]":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getLocalOfficeAddress()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Telephone":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getTelephoneNum()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Fax":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getFaxNumber()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Contact Person Name":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getContactName()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Designation":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getContactDesignation()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Contact Phone Number":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getContactPhoneNum()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Contact Email ID":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getContactEmailId()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Regional Head/Project Coordinator":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getRegionalHeadName()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Regional Head/Project Coordinator Mobile No":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getRegionalHeadPhoneNum()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Turnover Details":
                        List<List<JsonNode>> turnOverInfo = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getTurnOverDetails()).collect(Collectors.toList());
                        List<String> turnOverDetails = turnOverInfo.stream().map(item -> item.stream().map(x -> String.format("Year: %s, Total revenue: %s", x.get("year").asText(), x.get("revenue").asText())).collect(Collectors.joining("\n"))).collect(Collectors.toList());
                        fillData(sheet, turnOverDetails, row, styleMap);
                        break;
                    case "Client Reference #1":
                        List<JsonNode> clientOneRef = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getClientReferences()).collect(Collectors.toList());
                        List<String> projectResponse = clientReferenceData(clientOneRef, CommonModuleUtils.clientReferenceHeaders(), "Project 1");
                        fillData(sheet, projectResponse, row, styleMap);
                        break;
                    case "Client Reference #2":
                        List<JsonNode> clientTwoRef = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getClientReferences()).collect(Collectors.toList());
                        List<String> projectTwoResponse = clientReferenceData(clientTwoRef, CommonModuleUtils.clientReferenceHeaders(), "Project 2");
                        fillData(sheet, projectTwoResponse, row, styleMap);
                        break;
                    case "Client Reference #3":
                        List<JsonNode> clientThreeRef = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getClientReferences()).collect(Collectors.toList());
                        List<String> projectThreeResponse = clientReferenceData(clientThreeRef, CommonModuleUtils.clientReferenceHeaders(), "Project 3");
                        fillData(sheet, projectThreeResponse, row, styleMap);
                        break;
                    case "Similar Project #1":
                        List<JsonNode> similarProjectOne = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getSimilarProjectNature()).collect(Collectors.toList());
                        List<String> similarProjectOneRes = clientReferenceData(similarProjectOne, CommonModuleUtils.clientReferenceHeaders(), "Project 1");
                        fillData(sheet, similarProjectOneRes, row, styleMap);
                        break;
                    case "Similar Project #2":
                        List<JsonNode> similarProjectTwo = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getSimilarProjectNature()).collect(Collectors.toList());
                        List<String> similarProjectTwoRes = clientReferenceData(similarProjectTwo, CommonModuleUtils.clientReferenceHeaders(), "Project 2");
                        fillData(sheet, similarProjectTwoRes, row, styleMap);
                        break;
                    case "Similar Project #3":
                        List<JsonNode> similarProjectThree = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getSimilarProjectNature()).collect(Collectors.toList());
                        List<String> similarProjectThreeRes = clientReferenceData(similarProjectThree, CommonModuleUtils.clientReferenceHeaders(), "Project 3");
                        fillData(sheet, similarProjectThreeRes, row, styleMap);
                        break;
                    case "ESI Registration":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getEsiRegistration()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "EPF Registration":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getEpfRegistration()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "GST Registration":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getGstRegistration()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "PAN Number":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getPanNumber()).collect(Collectors.toList()), row, styleMap);
                        break;
                    case "Employee Strength":
                        List<JsonNode> employeeStrength = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getEmployeesStrength()).collect(Collectors.toList());
                        List<String> employeeStrengthInfo = employeeStrength.stream().map(item -> StreamSupport.stream(item.spliterator(), true).map(x -> String.format("Name: %s, Designation: %s, Qualification: %s, Total experience: %s", x.get("name").asText(), x.get("designation").asText(), x.get("qualification").asText(), x.get("totalExp").asText())).collect(Collectors.joining("\n"))).collect(Collectors.toList());
                        fillData(sheet, employeeStrengthInfo, row, styleMap);
                        break;

                    case "Capital Equipments":
                        List<JsonNode> capitalEquipment = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getCapitalEquipment()).collect(Collectors.toList());
                        List<String> capitalEquipmentInfo = capitalEquipment.stream().map(item -> StreamSupport.stream(item.spliterator(), true).map(x -> String.format("Equipment: %s, Quantity: %s, Own/Rented: %s, Capacity/Size: %s, Age/Condition: %s", x.get("description").asText(), x.get("quantity").asText(), x.get("own_rented").asText(), x.get("capacity_size").asText(), x.get("age_condition").asText())).collect(Collectors.joining("\n"))).collect(Collectors.toList());
                        fillData(sheet, capitalEquipmentInfo, row, styleMap);
                        break;

                    case "Safety Policy Manual":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getSafetyPolicyManual()).collect(Collectors.toList()), row, styleMap);
                        break;

                    case "PPE to Staff":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getPpeToStaff()).collect(Collectors.toList()), row, styleMap);
                        break;

                    case "PPE to Work Men":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getPpeToWorkMen()).collect(Collectors.toList()), row, styleMap);
                        break;

                    case "Safety Office Availability":
                        fillData(sheet, comparisonDtos.stream().map(item -> item.getApplicationFormDto().getSafetyOfficeAvailability()).collect(Collectors.toList()), row, styleMap);
                        break;

                    case "Financial Information":
                        List<JsonNode> finance = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getFinancialInformation()).collect(Collectors.toList());
                        List<String> financialInfo = finance.stream().map(item -> StreamSupport.stream(item.spliterator(), true).map(x -> String.format("Financial Year: %s, Gross Turnover: %s, Net profit before taxes: %s, Profit after tax: %s, Current Assets: %s, Current Liabilities:%s", x.get("f_year").asText(), x.get("gross_turnover").asText(), x.get("net_profit").asText(), x.get("profit_after_tax").asText(), x.get("current_assets").asText(), x.get("current_liabilities").asText())).collect(Collectors.joining("\n"))).collect(Collectors.toList());
                        fillData(sheet, financialInfo, row, styleMap);
                        break;

                    case "Company Bankers":
                        List<JsonNode> companyBankers = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getCompanyBankers()).collect(Collectors.toList());
                        List<String> bankerInfo = companyBankers.stream().map(item -> StreamSupport.stream(item.spliterator(), true).map(x -> String.format("Name: %s, Address: %s", x.get("name").asText(), x.get("address").asText())).collect(Collectors.joining("\n"))).collect(Collectors.toList());
                        fillData(sheet, bankerInfo, row, styleMap);
                        break;

                    case "Company Auditors":
                        List<JsonNode> companyAuditors = comparisonDtos.stream().map(item -> item.getApplicationFormDto().getCompanyAuditors()).collect(Collectors.toList());
                        List<String> auditorsInfo = companyAuditors.stream().map(item -> StreamSupport.stream(item.spliterator(), true).map(x -> String.format("Name: %s, Address: %s", x.get("name").asText(), x.get("address").asText())).collect(Collectors.joining("\n"))).collect(Collectors.toList());
                        fillData(sheet, auditorsInfo, row, styleMap);
                        break;
                }

            });

            workbook.write(outputStream);
            byte[] data = Files.readAllBytes(tempFile.toPath());
            fileDownloadDto.setEncodedResponse(Base64.encodeBase64String(data));
            fileDownloadDto.setFileName(String.format("Tender_%s_comparison.xlsx", tenderId));
            fileDownloadDto.setFileType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        } finally {
            Files.delete(tempFile.toPath());
        }
        return fileDownloadDto;
    }

    private void fillData(Sheet sheet, List<String> comparisonDtos, Row row, Map<String, CellStyle> style) {
        int i = 1;
        for (String dto : comparisonDtos) {
            sheet.setColumnWidth(i, 9999);
            Cell data = row.createCell(i);
            data.setCellValue(dto);
            if (i % 2 == 1) {
                data.setCellStyle(style.get("n+1"));
            } else {
                data.setCellStyle(style.get("n"));
            }
            i++;
        }
    }

    private Row createCell(Sheet sheet, int index, String cellValue, CellStyle style) {
        Row row = sheet.createRow(index);
        Cell cell = row.createCell(0);
        cell.setCellValue(cellValue);
        cell.setCellStyle(style);
        return row;
    }

    private List<String> clientReferenceData(List<JsonNode> clientOneRef, String[] textToFilter, String textToGet) {
        return clientOneRef.stream().map(ref -> {
            ArrayList<String> response = new ArrayList<>();

            Arrays.stream(textToFilter).forEach(item -> {
                Optional<JsonNode> nodeFound = StreamSupport.stream(ref.spliterator(), true).filter(x -> x.get("details").asText().equalsIgnoreCase(item)).findFirst();

                String valueForText = "";
                if (nodeFound.isPresent()) {
                    if (nodeFound.get().has(textToGet)) {
                        valueForText = nodeFound.get().get(textToGet).asText();
                    }
                }
                response.add(String.format("%s %s", item, valueForText));
            });

            return String.join("\n", response);
        }).collect(Collectors.toList());
    }

}
