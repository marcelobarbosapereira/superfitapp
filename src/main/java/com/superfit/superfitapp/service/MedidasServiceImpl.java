package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.medidas.HistoricoEvolucaoDTO;
import com.superfit.superfitapp.dto.medidas.MedidasCreateDTO;
import com.superfit.superfitapp.dto.medidas.MedidasResponseDTO;
import com.superfit.superfitapp.dto.medidas.MedidasUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Medidas;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.MedidasRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("medidasService")
public class MedidasServiceImpl implements MedidasService {

    private final MedidasRepository medidasRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public MedidasServiceImpl(MedidasRepository medidasRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.medidasRepository = medidasRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    public boolean isMedidaDoAluno(Long medidasId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return medidasRepository.existsByIdAndAlunoUserEmail(medidasId, email);
    }

    @Override
    public boolean isMedidaDoProfessor(Long medidasId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return medidasRepository.existsByIdAndAlunoProfessorUserEmail(medidasId, email);
    }

    @Override
    @Transactional
    public MedidasResponseDTO criar(MedidasCreateDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(dto.getAlunoId(), "alunoId")
        ).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // Verificar se o aluno pertence ao professor
        if (!aluno.getProfessor().getId().equals(professor.getId())) {
            throw new RuntimeException("Este aluno não pertence a você");
        }

        Medidas medidas = new Medidas();
        medidas.setData(dto.getData());
        medidas.setPeso(dto.getPeso());
        medidas.setPeito(dto.getPeito());
        medidas.setCintura(dto.getCintura());
        medidas.setQuadril(dto.getQuadril());
        medidas.setAluno(aluno);

        // Calcular IMC automaticamente
        medidas.calcularImc();

        medidas = medidasRepository.save(medidas);
        return toResponseDTO(medidas);
    }

    @Override
    public List<MedidasResponseDTO> listarTodas() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Buscar professor pelo email
        Professor professor = professorRepository.findByEmail(email).orElse(null);

        if (professor != null) {
            // Se for professor, retorna medidas de todos os seus alunos
            return medidasRepository.findByProfessorEmail(email)
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        // Se for aluno, retorna apenas suas medidas
        Aluno aluno = alunoRepository.findByEmail(email).orElse(null);
        if (aluno != null) {
            return medidasRepository.findByAlunoIdOrderByDataDesc(aluno.getId())
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @Override
    public List<MedidasResponseDTO> listarPorAluno(Long alunoId) {
        return medidasRepository.findByAlunoIdOrderByDataDesc(
                Objects.requireNonNull(alunoId, "alunoId")
        )
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HistoricoEvolucaoDTO obterHistoricoEvolucao(Long alunoId) {
        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(alunoId, "alunoId")
        ).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        List<Medidas> medidasList = medidasRepository.findByAlunoIdOrderByDataAsc(alunoId);

        List<MedidasResponseDTO> historico = medidasList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        // Calcular evolução
        HistoricoEvolucaoDTO.EvolucaoResumoDTO evolucao = null;
        if (!medidasList.isEmpty()) {
            Medidas primeira = medidasList.get(0);
            Medidas ultima = medidasList.get(medidasList.size() - 1);

            double pesoInicial = primeira.getPeso();
            double pesoAtual = ultima.getPeso();
            double diferencaPeso = pesoAtual - pesoInicial;

            double imcInicial = primeira.getImc() != null ? primeira.getImc() : 0.0;
            double imcAtual = ultima.getImc() != null ? ultima.getImc() : 0.0;
            double diferencaImc = imcAtual - imcInicial;

            evolucao = new HistoricoEvolucaoDTO.EvolucaoResumoDTO(
                    pesoInicial,
                    pesoAtual,
                    Math.round(diferencaPeso * 10.0) / 10.0,
                    imcInicial,
                    imcAtual,
                    Math.round(diferencaImc * 10.0) / 10.0
            );
        }

        return new HistoricoEvolucaoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getAltura(),
                aluno.getSexo(),
                historico,
                evolucao
        );
    }

    @Override
    public MedidasResponseDTO buscarPorId(Long id) {
        Medidas medidas = medidasRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Medidas não encontradas"));

        return toResponseDTO(medidas);
    }

    @Override
    @Transactional
    public MedidasResponseDTO atualizar(Long id, MedidasUpdateDTO dto) {
        Medidas medidas = medidasRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Medidas não encontradas"));

        medidas.setData(dto.getData());
        medidas.setPeso(dto.getPeso());
        medidas.setPeito(dto.getPeito());
        medidas.setCintura(dto.getCintura());
        medidas.setQuadril(dto.getQuadril());

        // Recalcular IMC
        medidas.calcularImc();

        medidas = medidasRepository.save(medidas);
        return toResponseDTO(medidas);
    }

    @Override
    public void remover(Long id) {
        if (!medidasRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Medidas não encontradas");
        }
        medidasRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

    private MedidasResponseDTO toResponseDTO(Medidas medidas) {
        return new MedidasResponseDTO(
                medidas.getId(),
                medidas.getData(),
                medidas.getPeso(),
                medidas.getImc(),
                medidas.getPeito(),
                medidas.getCintura(),
                medidas.getQuadril(),
                medidas.getAluno().getId(),
                medidas.getAluno().getNome()
        );
    }
}
