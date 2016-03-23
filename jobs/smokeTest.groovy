def rubies = ["2.1.6", "2.2.2"]

rubies.each { ruby ->
  job("ruby-${ruby}-osx-smoke-test") {
    description("Smoke test build artifacts on OSX")

    scm {
      git {
	remote {
	  github("jdenen/dow")
	}
	extensions {
	  cleanBeforeCheckout()
	}
      }
    }

    triggers {
      upstream("ruby-${ruby}-osx-build", "SUCCESS")
    }

    wrappers {
      rbenv("${ruby}") {
	ignoreLocalVersion()
	gems("bundler", "rake")
      }
    }

    steps {
      copyArtifacts("ruby-${ruby}-osx-build") {
	includePatterns("*.gz")
	buildSelector {
	  latestSuccessful(true)
	}
      }
      shell("tar -xzf dow-${ruby}-osx.tar.gz")
      shell("bundle check || bundle install")
      shell("bundle exec rake spec:smoke")
    }

    publishers {
      archiveJunit("result/*.xml")
      slackNotifications {
	projectChannel("#general")
	notifyFailure()
	notifyRepeatedFailure()
	notifyBackToNormal()
      }
      archiveArtifacts {
	pattern("dow-${ruby}-osx.tar.gz")
	onlyIfSuccessful()
      }
    }
  }
}
