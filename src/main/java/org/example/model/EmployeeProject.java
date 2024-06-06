package org.example.model;

import java.time.LocalDate;

public record EmployeeProject(int empId, int projectId, LocalDate dateFrom, LocalDate dateTo) {
}