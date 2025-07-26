package phi.elyoum8.controller;


//@Controller
//@RequestMapping("natega/students")
//@RequiredArgsConstructor
/*class OldStudentMVCController {
    private final StudentRepository studentRepository;

    @GetMapping("/{id}")
    public String getStudent(@PathVariable long id, Model model)
    {
        var student = studentRepository.findById(id).orElse(null);
        model.addAttribute("student", student);
        return "student-view";
    }


    @GetMapping()
    public String getAllByName(@RequestParam String name,Model model)
    {
        List<Student>students = studentRepository.findAllStudentsByNameStartsWith(name);
        model.addAttribute("students", students);
        return "students-view";
    }

    @GetMapping("/sorted")
    public String getAllSorted(@RequestParam Integer page , @RequestParam Integer size, Model model )
    {
        List<Student>students = studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
        model.addAttribute("students", students);
        return "students-view";
    }

    @GetMapping("/part")
    public String getAllBetween(@RequestParam Long from , @RequestParam Long to, Model model)
    {
        List<Student>students = studentRepository.findAllBetween(from, to);
        model.addAttribute("students", students);
        return "students-view";
    }
}*/




