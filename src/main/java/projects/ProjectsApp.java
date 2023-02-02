/**
 * 
 */
package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import projects.service.ProjectService;

/**
 * @author briandutremble
 *
 */
public class ProjectsApp {
	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project",
			"6) Add step to current project",
			"7) Add material to current project"
			
			);
	// @formatter:on
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new ProjectsApp().processUserSelection();

	}

	
	// This is the method by which the user selects an option (using a switch statement)
	private void processUserSelection() {
		boolean done = false;

		while (!done) {
			
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;
					
				case 2:
					listProjects();
					break;
				
				case 3:
					selectProject();
					break;
					
				case 4:
					updateProjectDetails();
					break;
					
				case 5:
					deleteProject();
					break;
					
				case 6:
					addStepToCurrentProject();
					break;
				
				case 7:
					addMaterialToCurrentProject();
					break;

				default:
					System.out.println("\n" + selection + " is not a valid menu selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}

	}
	
	private void addStepToCurrentProject() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou must first select a project to update. Please select a project.");
			return;
		}
		
		String stepText = getStringInput("Enter the step text");
		
		if(Objects.nonNull(stepText)) {
			Step step = new Step();
			
			step.setProjectId(curProject.getProjectId());
			step.setStepText(stepText);
			
			projectService.addStep(step);
			curProject = projectService.fetchProjectById(step.getProjectId());
		}
		
	}

	private void addMaterialToCurrentProject() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou must first select a project to update. Please select a project.");
			return;
		}
		
		String materialName = getStringInput("Enter the material to add");
		Integer materialQuantity = getIntInput("Enter the quantity");
		BigDecimal materialCost = getDecimalInput("Enter material cost per unit");
		
		if(Objects.nonNull(materialName)) {
			Material material = new Material();
			
			material.setProjectId(curProject.getProjectId());
			material.setMaterialName(materialName);
			material.setNumRequired(materialQuantity);
			material.setCost(materialCost);
			
			projectService.addMaterialToProject(material);
			curProject = projectService.fetchProjectById(material.getProjectId());
		}
		
	}

	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to delete");
		projectService.deleteProject(projectId);
		
		System.out.println("Project " + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
		
	}


	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou must first select a project to update. Please select a project.");
			return;
		}
		
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter project difficulty [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		
		Project project = new Project();
		
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		
	}


	// Takes the user's input and then calls the method to fetch the selected project
	private void selectProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
	
	}

	private List<Project> listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects");
		
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
		
		return projects;
	}

	// Asks the user to provide each of the attributes for the project to be created
	// and calls the addProject method to commit those changes
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
		//curProject = projectService.fetchProjectById(dbProject.getProjectId());
	}
	private boolean exitMenu() {
		System.out.println("\nExiting the menu. Thanks for using this program!");
		return true;
	}
	
	// The below methods are called whenever we want to ask the user for input
	private int getUserSelection() {
		printOperations();
		Integer op = getIntInput("Enter a menu selection");
		
		return Objects.isNull(op) ? -1 : op;
	}
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number");
		}
	}
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number");
		}
	}
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();
		
		return line.isBlank() ? null : line.trim();
	}
	
	// This prints the available menu options and informs the user whether a project is currently selected
	private void printOperations() {
		System.out.println();
		System.out.println("\nThese are the available selections. Press the enter key to quit:");
		
		operations.forEach(line -> System.out.println("    " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
			
		}else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
