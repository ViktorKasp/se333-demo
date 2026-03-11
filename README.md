# SE333 Final Project — AI-Powered Software Testing Agent

**Student:** Viktor Kasp  
**Course:** SE333 — Software Testing  
**Repository:** https://github.com/ViktorKasp/se333-demo  
**Application Tested:** [Spring PetClinic](https://github.com/spring-projects/spring-petclinic)

---

## Project Overview

This project implements an intelligent software testing agent using the **Model Context Protocol (MCP)**. The agent automatically generates, executes, and iterates on JUnit test cases to maximize JaCoCo code coverage on a Java Spring Boot application. It integrates with GitHub for version control automation and includes a custom Specification-Based Testing extension.

**Tech Stack:**
- Python 3.11+, FastMCP 2.x
- Java 17, Maven 3.9, Spring Boot (PetClinic)
- JaCoCo 0.8.14
- VS Code with GitHub Copilot Agent mode
- GitHub MCP Server

---

## MCP Tool API Documentation

### Tool 1: `add`
A basic arithmetic tool used to verify MCP server connectivity.

| Field | Details |
|-------|---------|
| **Input** | `a: int`, `b: int` |
| **Output** | `int` — sum of a and b |
| **Usage** | `add(1, 2)` → `3` |

---

### Tool 2: `parse_jacoco`
Parses a JaCoCo XML coverage report and returns per-class coverage metrics.

| Field | Details |
|-------|---------|
| **Input** | `xml_path: str` — path to `jacoco.xml` |
| **Output** | `dict` with list of classes, covered/missed lines, coverage %, uncovered methods |
| **Usage** | `parse_jacoco("target/site/jacoco/jacoco.xml")` |

**Example Output:**
```json
{
  "classes": [
    {
      "class": "org/springframework/samples/petclinic/owner/OwnerController",
      "covered_lines": 45,
      "missed_lines": 0,
      "coverage_percent": 100.0,
      "uncovered_methods": []
    }
  ]
}
```

---

### Tool 3: `boundary_value_analysis`
Generates boundary value test cases for a method parameter given a valid range.

| Field | Details |
|-------|---------|
| **Input** | `method_name: str`, `param_name: str`, `min_value: int`, `max_value: int`, `param_type: str` |
| **Output** | `dict` with 5 test cases (min, max, min-1, max+1, nominal) and a JUnit template |
| **Usage** | `boundary_value_analysis("addVisit", "petId", 1, 999, "int")` |

**Test Cases Generated:**
- Minimum boundary value (valid)
- Maximum boundary value (valid)
- Just below minimum (invalid)
- Just above maximum (invalid)
- Nominal/typical value (valid)

---

### Tool 4: `equivalence_class_generator`
Generates equivalence class test cases for valid and invalid input partitions.

| Field | Details |
|-------|---------|
| **Input** | `method_name: str`, `param_name: str`, `valid_values: list`, `invalid_values: list`, `param_description: str` |
| **Output** | `dict` with test cases per partition and a JUnit template |
| **Usage** | `equivalence_class_generator("getPet", "name", ["Buddy", "Max"], ["", null, "VeryLongName..."])` |

---

## Installation & Configuration Guide

### Prerequisites

Ensure the following are installed before starting:

```bash
node --version      # Must be 18+
java --version      # Must be 11+
mvn --version       # Must be 3.6+
git --version
python --version    # Must be 3.11+
```

### Step 1 — Install uv Package Manager

**Mac/Linux:**
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

**Windows (PowerShell):**
```powershell
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"
```

### Step 2 — Set Up the MCP Server

```powershell
mkdir se333-mcp-server
cd se333-mcp-server
uv init
uv venv
.venv\Scripts\activate        # Windows
# source .venv/bin/activate   # Mac/Linux
uv add "mcp[cli]" httpx fastmcp
```

### Step 3 — Configure main.py

Create `main.py` in the `se333-mcp-server` folder with the four MCP tools: `add`, `parse_jacoco`, `boundary_value_analysis`, and `equivalence_class_generator`. Run the server with:

```powershell
python main.py
```

The server will be available at: `http://127.0.0.1:8000/sse`

### Step 4 — Connect to VS Code

1. Press `Ctrl+Shift+P` → search **"MCP: Add Server"**
2. Enter URL: `http://127.0.0.1:8000/sse`
3. Name it: `se333-mcp-server`
4. Verify the server shows **"Running | 4 tools"** in `.vscode/mcp.json`

### Step 5 — Add GitHub MCP Server

In `.vscode/mcp.json`, add:

```json
"github": {
  "command": "npx",
  "args": ["-y", "@modelcontextprotocol/server-github"],
  "env": {
    "GITHUB_PERSONAL_ACCESS_TOKEN": "YOUR_TOKEN_HERE"
  }
}
```

Generate your token at: GitHub → Settings → Developer Settings → Personal Access Tokens → Tokens (classic). Required scopes: `repo`, `workflow`, `read:org`.

### Step 6 — Set Up the Client Project

```powershell
mkdir se333-demo
cd se333-demo
git clone https://github.com/spring-projects/spring-petclinic.git
cd spring-petclinic
mvn test jacoco:report
```

### Step 7 — Create the Agent Prompt

Create `.github/prompts/tester.prompt.md` with your agent instructions. In VS Code Chat (Agent mode), run:

```
Act on the instructions in tester.prompt.md
```

---

## Troubleshooting & FAQ

**Q: The MCP server won't start.**  
A: Make sure your virtual environment is activated (`.venv\Scripts\activate`) before running `python main.py`. If port 8000 is in use, add `port=8001` to `mcp.run()`.

**Q: VS Code shows the server as "Stopped".**  
A: The PowerShell window running `python main.py` must stay open. Never close it while working. If closed, navigate back to `se333-mcp-server` and rerun the server.

**Q: The agent answers math questions without calling the tool.**  
A: Switch VS Code chat to **Agent** mode (click the `</>` icon) and explicitly say "use the add tool to calculate 1+2".

**Q: `mvn test` fails with compilation errors.**  
A: The agent may have generated tests with incorrect imports. Ask the agent to "debug and fix the failing test" and it will resolve them automatically.

**Q: JaCoCo report not generated after `mvn test`.**  
A: Run `mvn test jacoco:report` explicitly. The report requires both commands.

**Q: GitHub push is rejected.**  
A: Make sure you're on a feature branch, not main. Run `git checkout -b feature/your-branch-name` first.

**Q: Merge conflicts on GitHub PR.**  
A: Run `git fetch origin`, then `git merge origin/main -X theirs` on your feature branch, then force push.

---
