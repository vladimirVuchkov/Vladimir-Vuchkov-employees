package org.example.controller;

import org.example.model.EmployeePair;
import org.example.model.EmployeeProject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePairCalculator {
    public static List<EmployeePair> findLongestWorkingPairs(final List<EmployeeProject> projects) {
        final Map<Integer, List<EmployeeProject>> projectsByEmployee = new HashMap<>();

        // Grouping projects by employees
        for (final EmployeeProject project : projects) {
            projectsByEmployee.computeIfAbsent(project.empId(), k -> new ArrayList<>()).add(project);
        }

        final List<EmployeePair> employeePairs = new ArrayList<>();
        final Map<String, Long> pairDurations = new HashMap<>();

        // Finding pairs of employees who have worked together on projects
        for (final Map.Entry<Integer, List<EmployeeProject>> entry : projectsByEmployee.entrySet()) {
            for (final EmployeeProject project : entry.getValue()) {
                for (final Map.Entry<Integer, List<EmployeeProject>> otherEntry : projectsByEmployee.entrySet()) {
                    if (!entry.getKey().equals(otherEntry.getKey())) {
                        for (final EmployeeProject otherProject : otherEntry.getValue()) {
                            if (project.projectId() == otherProject.projectId()) {
                                final LocalDate start = project.dateFrom().isAfter(otherProject.dateFrom()) ? project.dateFrom() : otherProject.dateFrom();
                                final LocalDate end = project.dateTo().isBefore(otherProject.dateTo()) ? project.dateTo() : otherProject.dateTo();
                                if (!start.isAfter(end)) {
                                    final long days = ChronoUnit.DAYS.between(start, end) + 1; // Add and last day
                                    final String pairKey = entry.getKey() < otherEntry.getKey() ?
                                            entry.getKey() + "-" + otherEntry.getKey() + "-" + project.projectId() :
                                            otherEntry.getKey() + "-" + entry.getKey() + "-" + project.projectId();
                                    pairDurations.put(pairKey, pairDurations.getOrDefault(pairKey, 0L) + days);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Convert the results to a list of EmployeePair
        for (final Map.Entry<String, Long> entry : pairDurations.entrySet()) {
            final String[] keys = entry.getKey().split("-");
            final int empId1 = Integer.parseInt(keys[0]);
            final int empId2 = Integer.parseInt(keys[1]);
            final int projectId = Integer.parseInt(keys[2]);
            employeePairs.add(new EmployeePair(empId1, empId2, projectId, entry.getValue()));
        }

        return employeePairs;
    }
}