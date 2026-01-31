# MOCO v2

<center>

![CI/CD](https://github.com/MoCo-v2/moco-v2-backend/actions/workflows/gradle.yml/badge.svg)

</center>

> study, project, mentoring platform

# index

- [outline](#outline)
- [gitflow](#gitflow)
- [architecture](#architecture)

# outline

Study, project, and mentoring platform used by programmers, planners, and designers

<br/><br/>

# Gitflow

> Describes JIRA-based gitflow

1. Create a JIRA ticket before you start working.

2. One ticket should preferably be a single commit.

3. Keep the commit graph as simple as possible.

4. Don't change the revision history of branches you share with each other.

5. Make sure to get reviews from reviewers.
6. merge your own pull requests.

```mermaid
gitGraph
    commit
    branch MOCO-01
    checkout MOCO-01
    commit
    commit
    checkout main
    merge MOCO-01
    branch MOCO-02
    checkout MOCO-02
    commit
    commit
    checkout main
    merge MOCO-02
    branch MOCO-03
    checkout MOCO-03
    commit
    commit
    checkout main
    merge MOCO-03
```

<br/>

# Architecture

![aws-architecture](https://github.com/wlswo/wlswo.github.io/blob/main/assets/images/moco-aws-architecture.png?raw=true)
