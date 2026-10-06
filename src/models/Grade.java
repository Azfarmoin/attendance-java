package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Grade record - stores assessments and computed grades for a student in a subject
 */
public class Grade {
    private String id;
    private String studentId;
    private String studentName;
    private String rollNumber;
    private String subjectId;
    private String subjectName;
    private String term;           // "2024"
    private String className;
    private List<Assessment> assessments;
    private double weightedPercentage;
    private double finalPercentage;
    private String letterGrade;
    private double gpaPoints;
    private boolean isPassed;
    private double attendancePercentage;

    public Grade() {
        this.assessments = new ArrayList<>();
        this.term = "2024";
    }

    public Grade(String id, String studentId, String studentName, String rollNumber,
                 String subjectId, String subjectName, String className) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.className = className;
        this.assessments = new ArrayList<>();
        this.term = "2024";
    }

    // Compute grades from assessments
    public void computeGrade() {
        if (assessments.isEmpty()) {
            weightedPercentage = 0;
            finalPercentage = 0;
            letterGrade = "N/A";
            gpaPoints = 0;
            isPassed = false;
            return;
        }

        double totalWeightedScore = 0;
        double totalWeight = 0;
        for (Assessment a : assessments) {
            double pct = (a.getTotalMarks() > 0) ? (a.getObtainedMarks() / a.getTotalMarks()) * 100 : 0;
            totalWeightedScore += pct * a.getWeightage();
            totalWeight += a.getWeightage();
        }

        weightedPercentage = totalWeight > 0 ? totalWeightedScore / totalWeight : 0;
        finalPercentage = weightedPercentage;

        // Apply attendance penalty if below 75%
        if (attendancePercentage > 0 && attendancePercentage < 75) {
            finalPercentage = Math.max(0, finalPercentage - 10);
        }

        // Assign letter grade
        if (finalPercentage >= 90) { letterGrade = "A+"; gpaPoints = 4.0; }
        else if (finalPercentage >= 85) { letterGrade = "A";  gpaPoints = 4.0; }
        else if (finalPercentage >= 80) { letterGrade = "B+"; gpaPoints = 3.5; }
        else if (finalPercentage >= 75) { letterGrade = "B";  gpaPoints = 3.0; }
        else if (finalPercentage >= 70) { letterGrade = "C+"; gpaPoints = 2.5; }
        else if (finalPercentage >= 65) { letterGrade = "C";  gpaPoints = 2.0; }
        else if (finalPercentage >= 60) { letterGrade = "D";  gpaPoints = 1.0; }
        else { letterGrade = "F"; gpaPoints = 0.0; }

        isPassed = finalPercentage >= 60;
    }

    public String getGradeColor() {
        if (letterGrade == null) return "#5a5a7a";
        return switch (letterGrade) {
            case "A+", "A" -> "#a8f000";
            case "B+", "B" -> "#60aaff";
            case "C+", "C" -> "#ffcc50";
            case "D"       -> "#ff8a50";
            case "F"       -> "#ff3d4a";
            default        -> "#5a5a7a";
        };
    }

    // Inner class for assessment
    public static class Assessment {
        private String type;     // "Assignment", "Quiz", "Midterm", "Final", "Project"
        private String title;
        private double totalMarks;
        private double obtainedMarks;
        private double weightage;
        private String date;
        private String remarks;

        public Assessment() {}
        public Assessment(String type, String title, double totalMarks, double obtainedMarks, double weightage) {
            this.type = type;
            this.title = title;
            this.totalMarks = totalMarks;
            this.obtainedMarks = obtainedMarks;
            this.weightage = weightage;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public double getTotalMarks() { return totalMarks; }
        public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }
        public double getObtainedMarks() { return obtainedMarks; }
        public void setObtainedMarks(double obtainedMarks) { this.obtainedMarks = obtainedMarks; }
        public double getWeightage() { return weightage; }
        public void setWeightage(double weightage) { this.weightage = weightage; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public double getPercentage() {
            return totalMarks > 0 ? (obtainedMarks / totalMarks) * 100 : 0;
        }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public List<Assessment> getAssessments() { return assessments; }
    public void setAssessments(List<Assessment> assessments) { this.assessments = assessments; }
    public double getWeightedPercentage() { return weightedPercentage; }
    public void setWeightedPercentage(double wp) { this.weightedPercentage = wp; }
    public double getFinalPercentage() { return finalPercentage; }
    public void setFinalPercentage(double fp) { this.finalPercentage = fp; }
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public double getGpaPoints() { return gpaPoints; }
    public void setGpaPoints(double gpaPoints) { this.gpaPoints = gpaPoints; }
    public boolean isPassed() { return isPassed; }
    public void setPassed(boolean passed) { isPassed = passed; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double ap) { this.attendancePercentage = ap; }
}
