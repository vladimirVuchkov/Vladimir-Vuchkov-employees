package org.example.controller;

import org.example.model.EmployeeProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<EmployeeProject> readCSV(final String filePath) throws IOException {
        final List<EmployeeProject> employeeProjects = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                final String[] values = line.split(", ");
                final int empId = Integer.parseInt(values[0]);
                final int projectId = Integer.parseInt(values[1]);
                final LocalDate dateFrom = LocalDate.parse(values[2], formatter);
                final LocalDate dateTo = values[3].equals("NULL") ? LocalDate.now() : LocalDate.parse(values[3], formatter);
                employeeProjects.add(new EmployeeProject(empId, projectId, dateFrom, dateTo));
            }
        }
        return employeeProjects;
    }
}