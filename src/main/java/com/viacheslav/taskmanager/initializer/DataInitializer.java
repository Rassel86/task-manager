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

    private final UserRepository userRepository;
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

        List<User> users = createUsers();
        List<Project> projects = createProjects(users);
        List<Task> tasks = createTasks(projects, users);
        createComments(tasks, users);

        log.info("Data initialization completed successfully!");
    }

    private List<User> createUsers() {
        log.debug("Creating users...");
        User user1 = User.builder()
                .email("jakovlev.vya4eslaw@mail.ru")
                .firstName("Viacheslav")
                .lastName("Iakovlev")
                .username("rassel86rus")
                .role(UserRole.ADMIN)
                .passwordHash(passwordEncoder.encode("savage69"))
                .createdAt(ZonedDateTime.now().minusDays(7))
                .build();

        User user5 = User.builder()
                .email("secondAmin@mail.ru")
                .firstName("Admin")
                .lastName("Second")
                .username("Admin2")
                .role(UserRole.ADMIN)
                .passwordHash(passwordEncoder.encode("Second123!"))
                .createdAt(ZonedDateTime.now().minusDays(6))
                .build();

        User user2 = User.builder()
                .email("petr@mail.ru")
                .firstName("Petr")
                .lastName("Petrov")
                .username("petrov11")
                .passwordHash(passwordEncoder.encode("petr99"))
                .createdAt(ZonedDateTime.now().minusDays(1))
                .build();

        User user3 = User.builder()
                .email("maria@mail.ru")
                .firstName("Maria")
                .lastName("Dudareva")
                .username("MeryDu")
                .passwordHash(passwordEncoder.encode("marydu"))
                .createdAt(ZonedDateTime.now().minusDays(3))
                .build();

        User user4 = User.builder()
                .email("user13@mainl.ru")
                .firstName("Oleg")
                .lastName("Olegovich")
                .username("olegik88")
                .passwordHash(passwordEncoder.encode("oleg11"))
                .createdAt(ZonedDateTime.now())
                .build();

        List<User> savedUsers = userRepository.saveAll(List.of(user1, user2, user3, user4, user5));
        log.info("Created {} users", savedUsers.size());
        return savedUsers;
    }

    private List<Project> createProjects(List<User> users) {
        log.debug("Creating projects...");

        User viacheslav = findUserByUsername(users, "rassel86rus");
        User maria = findUserByUsername(users, "MeryDu");
        User petr = findUserByUsername(users, "petrov11");

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

    private List<Task> createTasks(List<Project> projects, List<User> users) {
        User viacheslav = findUserByUsername(users, "rassel86rus");
        User maria = findUserByUsername(users, "MeryDu");
        User petr = findUserByUsername(users, "petrov11");

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

    private void createComments(List<Task> tasks, List<User> users) {
        log.debug("Creating comments...");

        User viacheslav = findUserByUsername(users, "rassel86rus");
        User maria = findUserByUsername(users, "MeryDu");
        User petr = findUserByUsername(users, "petrov11");
        User oleg = findUserByUsername(users, "olegik88");

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

    private User findUserByUsername(List<User> users, String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("User with username \"%s\" not found", username)));
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
