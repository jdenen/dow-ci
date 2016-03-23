# dow-ci

Jenkins CI/CD configuration for my [dow](https://github.com/jdenen/dow) application. The pipeline is generated via the [Jobs DSL](https://github.com/jenkinsci/job-dsl-plugin) plugin.

## Next steps

I could spend the next 10 days refining the pipeline and not be content with it. Here are some of the pieces I would like to implement in the future.

1. Smoke testing the Linux builds
2. Uploading Linux builds to s3
3. Building/smoke testing for Windows
4. Upload packages to staging and release buckets via promotion

## Setup

### Plugins

Install the following plugins and configure for your team:

1. Job DSL
2. GitHub
3. rbenv
4. jUnit
5. Copy Artifact
6. s3
7. Slack Notifications

### Slack caveat

The newest version of the Slack notifier plugin is not working with the Job DSL (at the moment). To work around this, manually install version 1.8.1 of the plugin.

### Seed job

Manually create a "seed" job that pulls from this repository and finds its groovy scripts (`**/*.groovy`) via a **Process Job DSLs** build step. Execute this seed job and all CI/CD jobs and views will be populated.
