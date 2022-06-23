package com.rainbowwolfer.myspacedemo1.services.application

class AppVersion(
	val major: Int,
	val minor: Int,
	val build: Int,
	val revision: Int,
) {
	companion object {
		fun parse(string: String): AppVersion? {
			val split = string.split('.')
			if (split.size != 4) {
				return null
			}
			val marjor = split[0].toIntOrNull() ?: return null
			val minor = split[1].toIntOrNull() ?: return null
			val build = split[2].toIntOrNull() ?: return null
			val revision = split[3].toIntOrNull() ?: return null
			
			return AppVersion(marjor, minor, build, revision)
		}
	}
	
	override fun toString(): String {
		return "$major.$minor.$build.$revision"
	}
	
	fun compare(version: AppVersion): CompareResult {
		return when {
			this.major < version.major -> CompareResult.Less
			this.minor < version.minor -> CompareResult.Less
			this.build < version.build -> CompareResult.Less
			this.revision < version.revision -> CompareResult.Less
			this.major > version.major -> CompareResult.Greater
			this.minor > version.minor -> CompareResult.Greater
			this.build > version.build -> CompareResult.Greater
			this.revision > version.revision -> CompareResult.Greater
			else -> CompareResult.Same
		}
	}
	
	enum class CompareResult {
		Same, Greater, Less
	}
}