package com.erp.hrm.service;

import com.erp.hrm.entity.*;
import com.erp.hrm.enums.*;
import com.erp.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative HRM data (departments, designations, employees, attendance,
 * leave types/requests, payroll, performance reviews, trainings) on first startup.
 */
@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class HrmDataInitializer {

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final TrainingRepository trainingRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(3)
    @Transactional
    public void seed() {
        if (departmentRepository.count() > 0) {
            return;
        }
        log.info("Seeding HRM demo data...");

        String[][] deptData = {
                {"Engineering", "Tanvir Ahmed"},
                {"Sales", "Mahbub Alam"},
                {"HR", "Tanjina Akhter"},
                {"Finance", "Rumana Islam"},
                {"Operations", "Rashed Khan"}
        };
        List<Department> departments = new ArrayList<>();
        for (String[] d : deptData) {
            departments.add(departmentRepository.save(Department.builder()
                    .name(d[0]).head(d[1])
                    .description(d[0] + " department of Meridian.")
                    .build()));
        }

        String[][] desigData = {
                {"Engineering", "Software Engineer"}, {"Engineering", "Senior Engineer"}, {"Engineering", "Engineering Manager"},
                {"Sales", "Sales Executive"}, {"Sales", "Sales Manager"},
                {"HR", "HR Officer"}, {"HR", "HR Manager"},
                {"Finance", "Accountant"}, {"Finance", "Finance Manager"},
                {"Operations", "Operations Executive"}, {"Operations", "Operations Manager"}
        };
        List<Designation> designations = new ArrayList<>();
        for (String[] dg : desigData) {
            Department dept = departments.stream().filter(x -> x.getName().equals(dg[0])).findFirst().orElse(departments.get(0));
            designations.add(designationRepository.save(Designation.builder()
                    .title(dg[1]).department(dept).build()));
        }

        String[][] empData = {
                {"Shariful", "Islam", "Male"}, {"Nusrat", "Jahan", "Female"}, {"Mahin", "Khan", "Male"},
                {"Farzana", "Akter", "Female"}, {"Rakibul", "Hasan", "Male"}, {"Sadia", "Afrin", "Female"},
                {"Imran", "Hossain", "Male"}, {"Tania", "Rahman", "Female"}, {"Sabbir", "Ahmed", "Male"},
                {"Mim", "Akhter", "Female"}, {"Fahad", "Karim", "Male"}, {"Sumona", "Begum", "Female"},
                {"Naeem", "Uddin", "Male"}, {"Lamia", "Chowdhury", "Female"}, {"Tariq", "Aziz", "Male"},
                {"Jannatul", "Ferdous", "Female"}, {"Mizanur", "Rahman", "Male"}, {"Shaila", "Parvin", "Female"},
                {"Asif", "Mahmud", "Male"}, {"Ruma", "Khatun", "Female"}
        };
        EmployeeStatus[] empStatuses = EmployeeStatus.values();
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < empData.length; i++) {
            String[] e = empData[i];
            Designation desig = designations.get(i % designations.size());
            employees.add(employeeRepository.save(Employee.builder()
                    .employeeId(String.format("EMP-%04d", i + 1))
                    .firstName(e[0]).lastName(e[1])
                    .email((e[0] + "." + e[1]).toLowerCase() + "@meridian.com.bd")
                    .phone("+8801" + (700000000 + i * 1234567L))
                    .department(desig.getDepartment())
                    .designation(desig)
                    .joinDate(LocalDate.now().minusDays(60L + i * 45L))
                    .status(i % 7 == 0 ? empStatuses[1] : EmployeeStatus.ACTIVE)
                    .gender(e[2])
                    .dateOfBirth(LocalDate.of(1990 + (i % 10), (i % 12) + 1, (i % 27) + 1))
                    .address("House " + (i + 1) + ", Road " + ((i % 20) + 1) + ", Dhaka 1212")
                    .bankAccount("BD" + (1000000000L + i * 7654321L))
                    .emergencyContact("+8801" + (900000000 + i * 111111L))
                    .basicSalary(BigDecimal.valueOf(30000L + (i % 13) * 9000L))
                    .build()));
        }

        AttendanceStatus[] attStatuses = AttendanceStatus.values();
        for (int i = 0; i < 30; i++) {
            Employee emp = employees.get(i % employees.size());
            LocalDate day = LocalDate.now().minusDays(i % 5);
            AttendanceStatus st = attStatuses[i % attStatuses.length];
            attendanceRepository.save(Attendance.builder()
                    .employee(emp).date(day).status(st)
                    .checkIn(st == AttendanceStatus.ABSENT ? null : LocalTime.of(9, (i % 30)))
                    .checkOut(st == AttendanceStatus.ABSENT ? null : LocalTime.of(18, (i % 30)))
                    .build());
        }

        String[][] leaveTypeData = {
                {"Annual Leave", "20"}, {"Sick Leave", "14"}, {"Casual Leave", "10"},
                {"Maternity Leave", "90"}, {"Unpaid Leave", "0"}
        };
        List<LeaveType> leaveTypes = new ArrayList<>();
        for (String[] lt : leaveTypeData) {
            leaveTypes.add(leaveTypeRepository.save(LeaveType.builder()
                    .name(lt[0]).daysPerYear(Integer.parseInt(lt[1])).build()));
        }

        LeaveStatus[] leaveStatuses = LeaveStatus.values();
        String[] reasons = {"Family vacation", "Fever and cold", "Personal work", "Childbirth", "Travel abroad", "Wedding ceremony"};
        for (int i = 0; i < 6; i++) {
            LocalDate start = LocalDate.now().plusDays(i * 3L);
            leaveRepository.save(Leave.builder()
                    .employee(employees.get(i % employees.size()))
                    .leaveType(leaveTypes.get(i % leaveTypes.size()))
                    .startDate(start).endDate(start.plusDays((i % 4) + 1))
                    .reason(reasons[i % reasons.length])
                    .status(leaveStatuses[i % leaveStatuses.length])
                    .build());
        }

        String period = String.format("%04d-%02d", LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        PayrollStatus[] payStatuses = PayrollStatus.values();
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            BigDecimal basic = emp.getBasicSalary();
            BigDecimal allowances = basic.multiply(BigDecimal.valueOf(0.30));
            BigDecimal deductions = basic.multiply(BigDecimal.valueOf(0.05));
            payrollRepository.save(Payroll.builder()
                    .employee(emp).period(period)
                    .basicSalary(basic).allowances(allowances).deductions(deductions)
                    .netSalary(basic.add(allowances).subtract(deductions))
                    .status(payStatuses[i % payStatuses.length])
                    .build());
        }

        String[] goals = {"Improve code quality and mentor juniors", "Exceed quarterly sales target",
                "Streamline onboarding process", "Reduce monthly closing time", "Optimize delivery operations"};
        for (int i = 0; i < 5; i++) {
            performanceReviewRepository.save(PerformanceReview.builder()
                    .employee(employees.get(i))
                    .period("Q" + ((i % 4) + 1) + "-" + LocalDate.now().getYear())
                    .rating((i % 5) + 1)
                    .goals(goals[i % goals.length])
                    .comments("Consistent performer with strong ownership.")
                    .build());
        }

        String[][] trainingData = {
                {"Spring Boot Advanced", "Kamruzzaman Rony", "Technical", "PLANNED"},
                {"Effective Leadership", "Farhana Yasmin", "Soft Skills", "ONGOING"},
                {"Workplace Safety", "Rashed Khan", "Compliance", "COMPLETED"},
                {"Sales Negotiation", "Mahbub Alam", "Sales", "PLANNED"}
        };
        for (int i = 0; i < trainingData.length; i++) {
            String[] t = trainingData[i];
            trainingRepository.save(Training.builder()
                    .name(t[0]).trainer(t[1]).type(t[2]).status(t[3])
                    .startDate(LocalDate.now().plusDays(7L * (i + 1)))
                    .durationDays((i % 3) + 1)
                    .cost(BigDecimal.valueOf((i + 1) * 25000L))
                    .build());
        }

        log.info("HRM demo data seeded: {} departments, {} designations, {} employees.",
                departments.size(), designations.size(), employees.size());
    }
}
