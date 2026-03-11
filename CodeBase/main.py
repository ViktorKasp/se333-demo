from fastmcp import FastMCP
import xml.etree.ElementTree as ET

mcp = FastMCP("SE333 Testing Agent 🚀")

@mcp.tool
def add(a: int, b: int) -> int:
    """Add two numbers"""
    return a + b

@mcp.tool
def parse_jacoco(xml_path: str) -> dict:
    """Parse a JaCoCo XML coverage report and return coverage summary"""
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        
        results = []
        for package in root.findall('.//package'):
            for cls in package.findall('class'):
                class_name = cls.get('name', '')
                uncovered_methods = []
                
                for method in cls.findall('method'):
                    for counter in method.findall('counter'):
                        if counter.get('type') == 'LINE':
                            if int(counter.get('covered', 0)) == 0:
                                uncovered_methods.append(method.get('name'))
                
                for counter in cls.findall('counter'):
                    if counter.get('type') == 'LINE':
                        covered = int(counter.get('covered', 0))
                        missed = int(counter.get('missed', 0))
                        total = covered + missed
                        coverage_pct = (covered / total * 100) if total > 0 else 0
                        results.append({
                            'class': class_name,
                            'covered_lines': covered,
                            'missed_lines': missed,
                            'coverage_percent': round(coverage_pct, 1),
                            'uncovered_methods': uncovered_methods
                        })
        
        return {'classes': results}
    except Exception as e:
        return {'error': str(e)}

@mcp.tool
def boundary_value_analysis(
    method_name: str,
    param_name: str,
    min_value: int,
    max_value: int,
    param_type: str = "int"
) -> dict:
    """
    Generate boundary value test cases for a method parameter.
    Tests min, max, min-1, max+1, and nominal values.
    """
    try:
        nominal = (min_value + max_value) // 2
        
        test_cases = [
            {
                "test_name": f"test_{method_name}_{param_name}_min",
                "value": min_value,
                "description": f"Minimum boundary value ({min_value})",
                "expected": "valid"
            },
            {
                "test_name": f"test_{method_name}_{param_name}_max",
                "value": max_value,
                "description": f"Maximum boundary value ({max_value})",
                "expected": "valid"
            },
            {
                "test_name": f"test_{method_name}_{param_name}_below_min",
                "value": min_value - 1,
                "description": f"Just below minimum ({min_value - 1})",
                "expected": "invalid"
            },
            {
                "test_name": f"test_{method_name}_{param_name}_above_max",
                "value": max_value + 1,
                "description": f"Just above maximum ({max_value + 1})",
                "expected": "invalid"
            },
            {
                "test_name": f"test_{method_name}_{param_name}_nominal",
                "value": nominal,
                "description": f"Nominal/typical value ({nominal})",
                "expected": "valid"
            }
        ]
        
        return {
            "method": method_name,
            "parameter": param_name,
            "type": param_type,
            "range": {"min": min_value, "max": max_value},
            "test_cases": test_cases,
            "junit_template": f"""
@Test
void test_{method_name}_{param_name}_boundaryValues() {{
    // Minimum boundary
    assertDoesNotThrow(() -> {method_name}({min_value}));
    // Maximum boundary  
    assertDoesNotThrow(() -> {method_name}({max_value}));
    // Below minimum - should throw or return invalid
    assertThrows(Exception.class, () -> {method_name}({min_value - 1}));
    // Above maximum - should throw or return invalid
    assertThrows(Exception.class, () -> {method_name}({max_value + 1}));
    // Nominal value
    assertDoesNotThrow(() -> {method_name}({nominal}));
}}"""
        }
    except Exception as e:
        return {"error": str(e)}

@mcp.tool
def equivalence_class_generator(
    method_name: str,
    param_name: str,
    valid_values: list,
    invalid_values: list,
    param_description: str = ""
) -> dict:
    """
    Generate equivalence class test cases for a method parameter.
    Creates test cases for valid and invalid partitions.
    """
    try:
        test_cases = []
        
        # Valid equivalence classes
        for i, value in enumerate(valid_values):
            test_cases.append({
                "test_name": f"test_{method_name}_{param_name}_valid_{i+1}",
                "value": value,
                "partition": "valid",
                "description": f"Valid equivalence class {i+1}: {value}"
            })
        
        # Invalid equivalence classes
        for i, value in enumerate(invalid_values):
            test_cases.append({
                "test_name": f"test_{method_name}_{param_name}_invalid_{i+1}",
                "value": value,
                "partition": "invalid",
                "description": f"Invalid equivalence class {i+1}: {value}"
            })
        
        # Generate JUnit test template
        valid_tests = "\n".join([
            f"    assertDoesNotThrow(() -> {method_name}({repr(v)})); // valid: {v}"
            for v in valid_values
        ])
        invalid_tests = "\n".join([
            f"    assertThrows(Exception.class, () -> {method_name}({repr(v)})); // invalid: {v}"
            for v in invalid_values
        ])
        
        return {
            "method": method_name,
            "parameter": param_name,
            "description": param_description,
            "valid_partitions": len(valid_values),
            "invalid_partitions": len(invalid_values),
            "test_cases": test_cases,
            "junit_template": f"""
@Test
void test_{method_name}_{param_name}_equivalenceClasses() {{
    // Valid equivalence classes
{valid_tests}
    // Invalid equivalence classes
{invalid_tests}
}}"""
        }
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    mcp.run(transport="sse")