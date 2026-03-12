package com.example.workflow.config;

import com.example.workflow.entity.GroupType;
import com.example.workflow.entity.User;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.enums.GroupRole;
import com.example.workflow.enums.Role;
import com.example.workflow.repository.GroupTypeRepository;
import com.example.workflow.repository.UserGroupMembershipRepository;
import com.example.workflow.repository.UserGroupRepository;
import com.example.workflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RepositoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class SeedDataConfig {

    @Bean
    CommandLineRunner seed(UserRepository userRepository,
                           PasswordEncoder encoder,
                           GroupTypeRepository groupTypeRepository,
                           UserGroupRepository userGroupRepository,
                           UserGroupMembershipRepository membershipRepository,
                           RepositoryService repositoryService) {
        return args -> {
            User admin = userRepository.findByEmail("admin").orElseGet(() -> userRepository.save(User.builder()
                    .email("admin")
                    .passwordHash(encoder.encode("admin"))
                    .role(Role.ADMIN)
                    .createdAt(Instant.now())
                    .build()));

            User demo = userRepository.findByEmail("demo").orElseGet(() -> userRepository.save(User.builder()
                    .email("demo")
                    .passwordHash(encoder.encode("demo"))
                    .role(Role.USER)
                    .createdAt(Instant.now())
                    .build()));

            GroupType department = groupTypeRepository.findByCode("department").orElseGet(() -> groupTypeRepository.save(GroupType.builder()
                    .code("department")
                    .name("Department")
                    .description("Default process owning group type")
                    .createdAt(Instant.now())
                    .build()));

            UserGroup demoGroup = userGroupRepository.findAll().stream()
                    .filter(g -> g.getKey().equals("demo-team"))
                    .findFirst()
                    .orElseGet(() -> userGroupRepository.save(UserGroup.builder()
                            .groupTypeId(department.getId())
                            .key("demo-team")
                            .name("Demo Team")
                            .description("Seeded group for demo process")
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build()));

            membershipRepository.findByUserIdAndGroupId(demo.getId(), demoGroup.getId())
                    .orElseGet(() -> membershipRepository.save(UserGroupMembership.builder()
                            .userId(demo.getId())
                            .groupId(demoGroup.getId())
                            .groupRole(GroupRole.EDITOR)
                            .createdAt(Instant.now())
                            .build()));

            membershipRepository.findByUserIdAndGroupId(admin.getId(), demoGroup.getId())
                    .orElseGet(() -> membershipRepository.save(UserGroupMembership.builder()
                            .userId(admin.getId())
                            .groupId(demoGroup.getId())
                            .groupRole(GroupRole.EDITOR)
                            .createdAt(Instant.now())
                            .build()));

            if (repositoryService.createProcessDefinitionQuery().processDefinitionKey("homeChores").count() == 0) {
                String xml = """
                        <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                        <definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:flowable=\"http://flowable.org/bpmn\" targetNamespace=\"Examples\"> 
                          <process id=\"homeChores\" name=\"Домашние дела\" isExecutable=\"true\"> 
                            <startEvent id=\"start\" />
                            <sequenceFlow id=\"f1\" sourceRef=\"start\" targetRef=\"wash\"/>
                            <userTask id=\"wash\" name=\"Помыть посуду\" flowable:assignee=\"${assignee}\"/>
                            <sequenceFlow id=\"f2\" sourceRef=\"wash\" targetRef=\"gw1\"/>
                            <exclusiveGateway id=\"gw1\"/>
                            <sequenceFlow id=\"f3\" sourceRef=\"gw1\" targetRef=\"laundry\"><conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${needLaundry == true}]]></conditionExpression></sequenceFlow>
                            <sequenceFlow id=\"f4\" sourceRef=\"gw1\" targetRef=\"pgwStart\"/>
                            <userTask id=\"laundry\" name=\"Запустить стирку\" flowable:assignee=\"${assignee}\"/>
                            <sequenceFlow id=\"f5\" sourceRef=\"laundry\" targetRef=\"pgwStart\"/>
                            <parallelGateway id=\"pgwStart\"/>
                            <userTask id=\"trash\" name=\"Вынести мусор\" flowable:assignee=\"${assignee}\"/>
                            <userTask id=\"vacuum\" name=\"Пропылесосить\" flowable:assignee=\"${assignee}\"/>
                            <sequenceFlow id=\"f6\" sourceRef=\"pgwStart\" targetRef=\"trash\"/>
                            <sequenceFlow id=\"f7\" sourceRef=\"pgwStart\" targetRef=\"vacuum\"/>
                            <parallelGateway id=\"pgwEnd\"/>
                            <sequenceFlow id=\"f8\" sourceRef=\"trash\" targetRef=\"pgwEnd\"/>
                            <sequenceFlow id=\"f9\" sourceRef=\"vacuum\" targetRef=\"pgwEnd\"/>
                            <endEvent id=\"end\"/>
                            <sequenceFlow id=\"f10\" sourceRef=\"pgwEnd\" targetRef=\"end\"/>
                          </process>
                        </definitions>
                        """;
                repositoryService.createDeployment().name("home-chores-seed").addString("home-chores.bpmn20.xml", xml).deploy();
            }
        };
    }
}
