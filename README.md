# dow-ci

Jenkins CI/CD configuration for my [dow](https://github.com/jdenen/dow) application.

## Jenkins plugins

Install the following plugins and configure for your team at the system level:

1. Job DSL
2. GitHub
3. rbenv
4. jUnit
5. Copy Artifact
6. s3
7. Slack Notifications

## Slack caveat

The newest version fo the Slack notifier plugin is not working with the Job DSL (at the moment). To work around this, manually install version 1.8.1 of the plugin.

## Jobs

Manually create a "seed" job that pulls from this repository and finds its groovy scripts (`**/*.groovy`) via a **Process Job DSLs** build step. Execute this seed job and all CI/CD jobs and views will be populated.
