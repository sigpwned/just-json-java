import sys
import re
from collections import Counter

test_dir = sys.argv[1]

sep = re.compile("[^a-zA-Z0-9]+")

verbs = { "y": "yes", "n": "no" }

tests_by_name = Counter()

tests = []

for line in sys.stdin:
    line = line.strip()
    if line.startswith("#") or line == "":
        continue
    
    parts = [ part.strip() for part in line.split(":") ]

    supported = "PASS" in line

    filename = parts[1]
        
    yn = filename[0]
        
    test_name = filename[2:-5]

    test_name_parts = [ part for part in sep.split(test_name) if part != "" ]

    verb = verbs[yn]

    test_name_pascal_case = "".join([ part[0].upper()+part[1:].lower() for part in test_name_parts ])

    tests_by_name[test_name_pascal_case] += 1

    test_instance = tests_by_name[test_name_pascal_case]

    test_suffix = "" if test_instance == 1 else "_" + str(test_instance)

    tests.append((supported, yn, filename, verb, test_name_pascal_case, test_instance, test_suffix))

tests.sort(key = lambda t: (0 if t[0] else 1, test_name_pascal_case, test_instance))

def print_test(test):
    supported, yn, filename, verb, test_name_pascal_case, test_instance, test_suffix = test

    print(f"/**")
    print(f" * {filename}")
    print(f" */")
    print(f"@Test")
    if not supported:
        print(f"@Ignore")
    print(f"public void {verb}{test_name_pascal_case}{test_suffix}() throws IOException {{")
    print(f"    // This test is currently DISABLED because the implementation is known not to pass it.")
    print(f"    // This is a \"{yn}\" test, so the file contains {'valid' if yn == 'y' else 'invalid'} JSON.")
    print(f"    IllegalArgumentException problem;")
    print(f"    try {{")
    print(f"        JustJson.parseValue(new String(Files.readAllBytes(new File(\"{test_dir}/{filename}\").toPath())));")
    print(f"        problem = null;")
    print(f"    }}")
    print(f"    catch(IllegalArgumentException e) {{")
    print(f"        problem = e;")
    print(f"    }}")
    print(f"    ")
    print(f"    {'assertNull' if yn=='y' else 'assertNotNull'}(problem);")
    print(f"}}")
    print(f"")
    

print("// SUPPORTED TESTS ///////////////////////////////////////////////////////////////////////////////")
for test in [ t for t in  tests if t[0] ]:
    print_test(test)

print("// UNSUPPORTED TESTS /////////////////////////////////////////////////////////////////////////////")
for test in [ t for t in  tests if not t[0] ]:
    print_test(test)
    

        
