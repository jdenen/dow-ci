def rubies = ["2.1.6", "2.2.2"]

rubies.each { ruby ->
  job("ruby-${ruby}-osx-deploy-s3") {
    description("Upload OSX package to S3")

    triggers {
      upstream("ruby-${ruby}-osx-smoke-test", "SUCCESS")
    }

    steps {
      copyArtifacts("ruby-${ruby}-osx-smoke-test") {
	includePatterns("*.gz")
	buildSelector {
	  latestSuccessful(true)
	}
      }
    }

    publishers {
      slackNotifications {
	projectChannel("#general")
	teamDomain("johnson-ci")
	notifyFailure()
	notifyRepeatedFailure()
	notifyBackToNormal()
	notifySuccess()
      }
      s3("dow-release") {
	entry("*.gz", "dow-release/ruby-${ruby}", "us-east-1") {
	  noUploadOnFailure()
	}
      }
    }
  }
}
