package com.hobbytogther.domain;

import com.hobbytogther.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Hobby.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hobby {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;  //인원모집

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public void addMemeber(Account account) {
        this.members.add(account);
    }
    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }
    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }
}
