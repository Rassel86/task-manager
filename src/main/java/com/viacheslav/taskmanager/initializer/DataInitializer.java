package com.viacheslav.taskmanager.initializer;

import com.viacheslav.taskmanager.model.*;
import com.viacheslav.taskmanager.model.enums.TagColor;
import com.viacheslav.taskmanager.model.enums.TaskPriority;
import com.viacheslav.taskmanager.model.enums.TaskStatus;
import com.viacheslav.taskmanager.model.enums.UserRole;
import com.viacheslav.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping initialization.");
            return;
        }

        List<UserAccount> userAccounts = createUsers();
        List<Project> projects = createProjects(userAccounts);
        List<Task> tasks = createTasks(projects, userAccounts);
        createComments(tasks, userAccounts);

        log.info("Data initialization completed successfully!");
    }

    private List<UserAccount> createUsers() {
        log.debug("Creating users...");

        UserAccount userAccount1 = UserAccount.builder()
                .contactEmail("jakovlev.vya4eslaw@mail.ru")
                .firstName("Viacheslav")
                .lastName("Iakovlev")
                .displayName("Rassel86rus")
                .role(UserRole.ADMIN)
                .createdAt(ZonedDateTime.now().minusDays(7))
                .build();

        Credentials cred1 = Credentials.builder()
                .userAccount(userAccount1)
                .login(userAccount1.getContactEmail())
                .passwordHash(passwordEncoder.encode("Savage69!"))
                .build();

        userAccount1.setCredentials(cred1);

        UserAccount userAccount2 = UserAccount.builder()
                .contactEmail("secondAdmin@mail.ru")
                .firstName("Admin")
                .lastName("Second")
                .displayName("Admin2")
                .role(UserRole.ADMIN)
                .createdAt(ZonedDateTime.now().minusDays(6))
                .build();

        Credentials cred2 = Credentials.builder()
                .userAccount(userAccount2)
                .login(userAccount2.getContactEmail())
                .passwordHash(passwordEncoder.encode("Second123!"))
                .build();

        userAccount2.setCredentials(cred2);

        UserAccount userAccount3 = UserAccount.builder()
                .contactEmail("petr@mail.ru")
                .firstName("Petr")
                .lastName("Petrov")
                .displayName("Petrov11")
                .createdAt(ZonedDateTime.now().minusDays(1))
                .build();

        Credentials cred3 = Credentials.builder()
                .userAccount(userAccount3)
                .login(userAccount3.getContactEmail())
                .passwordHash(passwordEncoder.encode("Petruxa99!"))
                .build();

        userAccount3.setCredentials(cred3);

        UserAccount userAccount4 = UserAccount.builder()
                .contactEmail("maria@mail.ru")
                .firstName("Maria")
                .lastName("Dudareva")
                .displayName("MaryDu99")
                .createdAt(ZonedDateTime.now().minusDays(3))
                .build();

        Credentials cred4 = Credentials.builder()
                .userAccount(userAccount4)
                .login(userAccount4.getContactEmail())
                .passwordHash(passwordEncoder.encode("Marydu99!"))
                .build();

        userAccount4.setCredentials(cred4);

        UserAccount userAccount5 = UserAccount.builder()
                .contactEmail("user13@mainl.ru")
                .firstName("Oleg")
                .lastName("Olegovich")
                .displayName("Olegik88")
                .createdAt(ZonedDateTime.now())
                .build();

        Credentials cred5 = Credentials.builder()
                .userAccount(userAccount5)
                .login(userAccount5.getContactEmail())
                .passwordHash(passwordEncoder.encode("Oleg11rus!"))
                .build();

        userAccount5.setCredentials(cred5);

        List<UserAccount> savedUserAccounts = userRepository.saveAll(List.of(userAccount1, userAccount2, userAccount3, userAccount4, userAccount5));
        log.info("Created {} users", savedUserAccounts.size());
        return savedUserAccounts;
    }

    private List<Project> createProjects(List<UserAccount> userAccounts) {
        log.debug("Creating projects...");

        UserAccount viacheslav = findUserByUsername(userAccounts, "Rassel86rus");
        UserAccount maria = findUserByUsername(userAccounts, "MaryDu99");
        UserAccount petr = findUserByUsername(userAccounts, "Petrov11");

        Project personalProject = Project.builder()
                .name("Personal tasks")
                .description("My personal affairs and purchases")
                .owner(maria)
                .build();

        Project teamProject = Project.builder()
                .name("Task Manager Development")
                .description("The team's main project")
                .owner(viacheslav)
                .build();

        Project petrProject = Project.builder()
                .name("Learning Spring Boot")
                .description("Learning projects and experiments")
                .owner(petr)
                .build();

        List<Project> savedProjects = projectRepository.saveAll(
                List.of(personalProject, teamProject, petrProject));
        log.info("Created {} projects", savedProjects.size());
        return savedProjects;
    }

    private Set<Tag> createTags() {
        log.info("Creating tags...");
        Set<Tag> tags = Set.of(
                Tag.builder().name("Feature").color(TagColor.PURPLE).build(),
                Tag.builder().name("Urgent").color(TagColor.RED).build(),
                Tag.builder().name("TestTag").build(),
                Tag.builder().name("Bug").color(TagColor.BLUE).build()
        );
        List<Tag> savedTagsList = tagRepository.saveAll(tags);
        log.info("Created {} tags", savedTagsList.size());
        return new HashSet<>(savedTagsList);
    }

    private List<Task> createTasks(List<Project> projects, List<UserAccount> userAccounts) {
        UserAccount viacheslav = findUserByUsername(userAccounts, "Rassel86rus");
        UserAccount maria = findUserByUsername(userAccounts, "MaryDu99");
        UserAccount petr = findUserByUsername(userAccounts, "Petrov11");

        Project personalProject = findProjectByName(projects, "Personal tasks");
        Project teamProject = findProjectByName(projects, "Task Manager Development");

        Set<Tag> tags = createTags();
        Tag featureTag = findTagByName(tags, "Feature");
        Tag urgentTag = findTagByName(tags, "Urgent");
        Tag testTag = findTagByName(tags, "TestTag");
        Tag bugTag = findTagByName(tags, "Bug");

        Task task1 = Task.builder()
                .title("Разработать API")
                .description("Создать REST контроллеры для задач")
                .status(TaskStatus.TO_DO)
                .priority(TaskPriority.HIGH)
                .author(viacheslav)
                .assignee(petr)
                .tags(Set.of(bugTag, featureTag, urgentTag))
                .project(teamProject)
                .build();

        Task task2 = Task.builder()
                .title("Написать тесты")
                .description("Покрыть код юнит-тестами")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.LOW)
                .author(petr)
                .assignee(maria)
                .tags(Set.of(testTag, urgentTag))
                .project(teamProject)
                .build();

        Task task3 = Task.builder()
                .title("Обновить документацию")
                .description("Добавить описание новых эндпоинтов")
                .author(viacheslav)
                .assignee(null)
                .tags(Set.of(featureTag))
                .project(teamProject)
                .build();

        Task task4 = Task.builder()
                .title("Купить продукты")
                .description("Молоко, хлеб, яйца")
                .status(TaskStatus.TO_DO)
                .priority(TaskPriority.LOW)
                .author(maria)
                .assignee(viacheslav)
                .project(personalProject)
                .build();

        List<Task> savedTasks = taskRepository.saveAll(List.of(task1, task2, task3, task4));
        log.info("Created {} tasks", savedTasks.size());
        return savedTasks;
    }

    private void createComments(List<Task> tasks, List<UserAccount> userAccounts) {
        log.debug("Creating comments...");

        UserAccount viacheslav = findUserByUsername(userAccounts, "Rassel86rus");
        UserAccount maria = findUserByUsername(userAccounts, "MaryDu99");
        UserAccount petr = findUserByUsername(userAccounts, "Petrov11");
        UserAccount oleg = findUserByUsername(userAccounts, "Olegik88");

        Task apiTask = tasks.stream()
                .filter(task -> task.getTitle().contains("API"))
                .findFirst()
                .orElseThrow();

        Task testTask = tasks.stream()
                .filter(task -> task.getTitle().contains("тесты"))
                .findFirst()
                .orElseThrow();

        Comment comment1 = Comment.builder()
                .text("Нужно обсудить архитектуру на митинге")
                .author(maria)
                .task(apiTask)
                .build();

        Comment comment2 = Comment.builder()
                .text("Архитектуру какого города?)")
                .author(oleg)
                .task(apiTask)
                .build();

        Comment comment3 = Comment.builder()
                .text("Как скоро будут готовы тесты?")
                .author(viacheslav)
                .task(testTask)
                .build();

        Comment comment4 = Comment.builder()
                .text("Я уже начал писать тесты, будет готово завтра")
                .author(petr)
                .task(testTask)
                .build();

        List<Comment> savedComments = commentRepository.saveAll(
                List.of(comment1, comment2, comment3, comment4));
        log.info("Created {} comments", savedComments.size());
    }

    private UserAccount findUserByUsername(List<UserAccount> userAccounts, String username) {
        return userAccounts.stream()
                .filter(user -> user.getDisplayName().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("UserAccount with username \"%s\" not found", username)));
    }

    private Project findProjectByName(List<Project> projects, String name) {
        return projects.stream()
                .filter(project -> project.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Project with name \"%s\" not found", name)
                ));
    }

    private Tag findTagByName(Set<Tag> tags, String name) {
        return tags.stream()
                .filter(tag -> tag.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tag not found: " + name));
    }
}
