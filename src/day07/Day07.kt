package day07

import readInput

interface FSNode {
    val size: Int
    val name: String
}

class File(override val size: Int, override val name: String) : FSNode {
    override fun toString(): String {
        return "File '$name' ($size size)"
    }
}

class Directory(override val name: String) : FSNode {
    constructor(name: String, parentDir: Directory?) : this(name) {
        parent = parentDir ?: this
    }

    override val size: Int
        get() = children.sumOf { node -> node.size }

    val children: MutableList<FSNode> = mutableListOf()

    private val files: List<File>
        get() = children.filterIsInstance<File>()

    private val subdirectories: List<Directory>
        get() = children.filterIsInstance<Directory>()

    lateinit var parent: Directory

    fun cd(childDirectoryName: String): Directory {
        return when (childDirectoryName) {
            "/" -> rootDirectory
            ".." -> parent
            else -> {
                var childDirectory =
                    subdirectories.find { node -> node.name == childDirectoryName }
                if (childDirectory == null) {
                    childDirectory = Directory(childDirectoryName, this)
                    children.add(childDirectory)
                }
                childDirectory
            }
        }
    }

    fun tryAddChild(childNode: FSNode) {
        if (children.find { node -> node.name == childNode.name } == null)
            children.add(childNode)
    }

    fun filterRecursive(predicate: (FSNode) -> Boolean): List<FSNode> {
        val results = ArrayList<FSNode>()
        if (predicate(this))
            results.add(this)
        files.filterTo(results, predicate)
        for (subdir in subdirectories) {
            results.addAll(subdir.filterRecursive(predicate))
        }
        return results
    }

    override fun toString(): String {
        return "Dir $name (${children.size} files)"
    }
}

var rootDirectory = Directory("/", null)

fun parseFileTree(input: List<String>): Directory {
    rootDirectory = Directory("/", null)
    var currentDirectory: Directory = rootDirectory
    for (line in input) {
        if (line.startsWith("$ cd")) {
            val directoryToGo = line.substringAfter("$ cd ")
            currentDirectory = currentDirectory.cd(directoryToGo)
        } else if (line.startsWith("dir ")) {
            val directoryName = line.substringAfter("dir ")
            val listedDirectory = Directory(directoryName, currentDirectory)
            currentDirectory.tryAddChild(listedDirectory)
        } else if (line == "$ ls") {
            // Handling of file list is in the following branch
        } else {
            val size = line.substringBefore(" ").toInt()
            val fileName = line.substringAfter(" ")
            val listedFile = File(size, fileName)
            currentDirectory.tryAddChild(listedFile)
        }
    }
    return rootDirectory
}

fun toScript(input: List<String>): Directory {
    for (line in input) {
        if (line == "$ ls")
            ; // do nothing
        else if (line.startsWith("$ cd"))
            println(line.substringAfter("$ "))
        else if (line.startsWith("dir ")) {
            val directoryName = line.substringAfter("dir ")
            println("mkdir $directoryName")
        } else {
            val size = line.substringBefore(" ").toInt()
            val fileName = line.substringAfter(" ")
            println("truncate -s $size $fileName")
        }

    }
    return rootDirectory
}

fun main() {
    fun part1(input: List<String>): Int {
        val root = parseFileTree(input)
        val directoriesLessThan100000 = root.filterRecursive { node -> node is Directory && node.size <= 100000 }
        return directoriesLessThan100000.sumOf { node -> node.size }
    }

    fun part2(input: List<String>): Int {
        val root = parseFileTree(input)
        val fileSystemSize = 70000000
        val minimumFreeSpace = 30000000
        val freeSpace = fileSystemSize - root.size
        val toFreeAtLeast = minimumFreeSpace - freeSpace
        check(toFreeAtLeast > 0)
        val candidateDirectories = root.filterRecursive { node -> node is Directory && node.size >= toFreeAtLeast }
        val smallestDir = candidateDirectories.minByOrNull { it.size }!!
        return smallestDir.size
    }

    val testInput = readInput("sample_data", 7)
    println(part1(testInput))
    check(part1(testInput) == 95437)

    val mainInput = readInput("main_data", 7)
    println(part1(mainInput))
    check(part1(mainInput) == 1543140)

//    println(part2(testInput))
//    check(part2(testInput) == 0)
    println(part2(mainInput))
    check(part2(mainInput) == 1117448)
}
