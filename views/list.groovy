def views = ["test", "build", "deploy"]
def platforms = ["osx", "linux"]

views.each { view ->
  listView("dow-${view}") {
    jobs {
      regex(/.*(${view}).*/)
    }
    columns {
      status()
      weather()
      name()
      lastSuccess()
      lastFailure()
      lastDuration()
    }
  } 
}

platforms.each { pf ->
  listView("${pf}") {
    jobs {
      regex(/.*${pf}.*/)
    }
    columns {
      status()
      weather()
      name()
      lastSuccess()
      lastFailure()
      lastDuration()
    }
  }
}
