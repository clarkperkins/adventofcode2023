@file:JvmName("Main")

package com.clarkperkins.adventofcode

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import kotlin.reflect.full.createInstance


fun main(args: Array<String>) = Cli().main(args)

class Cli : CliktCommand() {
    init {
        subcommands(Day::class.sealedSubclasses.map { it.createInstance() })
    }

    override fun run() = Unit
}
