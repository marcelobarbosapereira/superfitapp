package com.superfit.superfitapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.superfit.superfitapp.dto.admin.GestorCreateDTO;
import com.superfit.superfitapp.dto.admin.GestorResponseDTO;
import com.superfit.superfitapp.dto.admin.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.admin.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.admin.ProfessorUpdateDTO;
import com.superfit.superfitapp.dto.admin.AlunoCreateDTO;
import com.superfit.superfitapp.dto.admin.AlunoResponseDTO;
import com.superfit.superfitapp.dto.admin.AlunoUpdateDTO;
import com.superfit.superfitapp.service.AdminService;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===============================
    // GESTORES
    // ===============================

    @PostMapping("/gestores")
    public ResponseEntity<GestorResponseDTO> criarGestor(
            @Valid @RequestBody GestorCreateDTO dto
    ) {
        GestorResponseDTO response = adminService.cadastrarGestor(dto);
        return ResponseEntity.ok(response);
    }

    // ===============================
    // PROFESSORES
    // ===============================

    @PostMapping("/professores")
    public ResponseEntity<ProfessorResponseDTO> criarProfessor(
            @Valid @RequestBody ProfessorCreateDTO dto
    ) {
        ProfessorResponseDTO response = adminService.cadastrarProfessor(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/professores")
    public ResponseEntity<List<ProfessorResponseDTO>> listarProfessores() {
        List<ProfessorResponseDTO> professores = adminService.listarProfessores();
        return ResponseEntity.ok(professores);
    }

    @PutMapping("/professores/{id}")
    public ResponseEntity<ProfessorResponseDTO> atualizarProfessor(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorUpdateDTO dto
    ) {
        ProfessorResponseDTO response = adminService.atualizarProfessor(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/professores/{id}")
    public ResponseEntity<Void> deletarProfessor(@PathVariable Long id) {
        adminService.excluirProfessor(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // ALUNOS
    // ===============================

    @PostMapping("/alunos")
    public ResponseEntity<AlunoResponseDTO> criarAluno(
            @Valid @RequestBody AlunoCreateDTO dto
    ) {
        AlunoResponseDTO response = adminService.cadastrarAluno(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alunos")
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunos() {
        List<AlunoResponseDTO> alunos = adminService.listarAlunos();
        return ResponseEntity.ok(alunos);
    }

    @PutMapping("/alunos/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizarAluno(
            @PathVariable Long id,
            @Valid @RequestBody AlunoUpdateDTO dto
    ) {
        AlunoResponseDTO response = adminService.atualizarAluno(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        adminService.excluirAluno(id);
        return ResponseEntity.noContent().build();
    }

}
