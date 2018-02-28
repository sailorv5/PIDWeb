package com.isfce.pidw.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.isfce.pidw.data.ICoursJpaDAO;
import com.isfce.pidw.data.IEtudiantJpaDAO;
import com.isfce.pidw.data.IEvaluationJpaDAO;
import com.isfce.pidw.data.IModuleJpaDAO;
import com.isfce.pidw.data.IProfesseurJpaDAO;
import com.isfce.pidw.data.IUsersJpaDAO;
import com.isfce.pidw.filter.EvaluationKey;
import com.isfce.pidw.model.Cours;
import com.isfce.pidw.model.Evaluation;
import com.isfce.pidw.model.ListeEvaluations;
import com.isfce.pidw.model.Module;
import com.isfce.pidw.model.Users;

@Controller
@RequestMapping("/evaluation")
public class EvaluationController {
	// Logger
	final static Logger logger = Logger.getLogger(EvaluationController.class);

	private IModuleJpaDAO moduleDAO;
	private ICoursJpaDAO coursDAO;
	private IProfesseurJpaDAO professeurDAO;
	private IEtudiantJpaDAO etudiantDAO;
	private IUsersJpaDAO<Users> usersDAO;
	private IEvaluationJpaDAO evaluationDAO;
	
	private String className = "EvaluationController" ;

	@Autowired
	public EvaluationController(IModuleJpaDAO moduleDAO, IProfesseurJpaDAO professeurDAO, ICoursJpaDAO coursDAO, IEtudiantJpaDAO etudiantDAO, IUsersJpaDAO<Users> usersDAO, IEvaluationJpaDAO evaluationDAO ) {
		super();
		this.moduleDAO	 = moduleDAO;
		this.professeurDAO	 = professeurDAO;
		this.coursDAO	 = coursDAO;
		this.etudiantDAO	 = etudiantDAO ;
		this.evaluationDAO = evaluationDAO;

		this.usersDAO = usersDAO;
	}

	

    @ResponseBody
	@RequestMapping(value = "/json")
	public String testJson(Model model) {
	    System.out.printf( "["+  this.getClass().getSimpleName() + "]"  +  "[testJson]"  +  "[]" );
	    
        // Converts a collection object into JSON string
        Gson gson = new Gson();
        String jsonObjecs = gson.toJson( etudiantDAO.findAll()  );
        System.out.println("jsonSjsonObjecstudents = " + jsonObjecs);
        

	    return jsonObjecs ;
	}
    
    
    
	@RequestMapping(value = { "/liste/{codeUser}", "/liste" })
	public String listeModules(@PathVariable Optional<String> codeUser, Model model, Authentication authentication) {
		
		System.out.printf( "["+  this.getClass().getSimpleName() + "]"  +  "[listeModules]"  +  "[]" );
		
		
		model.addAttribute("module", null );
		
		return "" ;
	}
	
	
	
	
	
	
//		/*
	@RequestMapping(value = { "/{code}"  },  method = RequestMethod.GET)  
	public String addUpdateEvaluationTest(
			
			@PathVariable Optional<String> code,
			@ModelAttribute Evaluation evaluation,
//			@RequestParam(value = "session", required = true) String session,
			
//			@RequestParam(value = "session", required = false) Optional<Integer> session,
			
			Model model
			, HttpServletRequest request
	) {
		
		System.out.printf( "*["+  this.getClass().getSimpleName() + "]"  +  "[addUpdateEvaluationTest]"  +  "[] \n" );
		
		System.out.println(  model.toString()      );
		System.out.println(  code.get()      );
		System.out.println(  request.toString()      );
		
		
		return "module/addEvaluation" ;
	}
//		*/
	
	
	
//	evaluation/IVTE-1-A/1
	
	
	@RequestMapping(value = { "/{code}/"  },  method = RequestMethod.POST)  	//		, "/{code}/{session}"
	public String addUpdateEvaluation(
//			HttpServletRequest request,
			@PathVariable Optional<String> code,
//			@RequestParam(value="session", required=false) Optional<Integer> session,
			@ModelAttribute Evaluation evaluation,
			Model model /* , Authentication authentication */
	) {
		
		System.out.printf( "*["+  this.getClass().getSimpleName() + "]"  +  "[addUpdateEvaluation]"  +  "[] \n" );
		
		System.out.println(  model.toString()      );
		System.out.println(  code.get()      );
		
		
//		System.out.println(  request.getParameterMap().toString()      );
		
		return "module/addEvaluation" ;
		
		

	}
	
	
	
	
	
	
	@RequestMapping(value = { "/update"  },  method = RequestMethod.POST)  
	public String addUpdateEvaluationPost(
			
			@ModelAttribute List<Evaluation> evaluation,
			Model model /* , Authentication authentication */) {
		
		
		System.out.printf( "*["+  this.getClass().getSimpleName() + "]"  +  "[addUpdateEvaluationPost]"  +  "[]" );
		System.out.println(model);
		
	
		return "module/listeModules" ;
	}
	
	
	
	
	
	
	

	@RequestMapping(value = "/{code}/add" )
	public String addEvaluation(@PathVariable String code, Model model) {
		
		System.out.printf( "*["+  this.getClass().getSimpleName() + "]"  +  "[addEvaluation]"  +  "[]" );
		
		// Vérifie si on ne recoit pas le module suite à une redirection
		if (!model.containsAttribute("module")) {
			logger.debug("Recherche le module: " + code);
			// recherche le module dans la liste
			// Vérifie si le module existe
			Module module = moduleDAO.findOne(code);
			// gestion spécifique pour la non présence du module.
			if (module == null) {
				logger.debug("Problème : Not found");
				throw new NotFoundException("Ce module existe déjà ", code);
			}
			
			
			// Ajout au Modèle
			model.addAttribute("module", module);
		} else
			logger.debug("Utilisation d'un FlashAttribute pour le module: " + code);
		
		Module module = moduleDAO.findOne(code);
		Cours c = coursDAO.findOne(module.getCours().getCode());
		c.setSections(coursDAO.coursSection(c.getCode()));
		module.setCours(c);
		module.setEtudiants(etudiantDAO.getEtudiantsOfModule(code));
		
		List<String> etudiantOfModule = new ArrayList<>();
		etudiantOfModule = moduleDAO.getFkEtudiantsOfModule(code);

		Integer nbrSession = getSessionOfEvaluation(code);
		Evaluation.SESSION theSession = null;

		if(nbrSession == 0) {
			theSession = Evaluation.SESSION.PREMIERE;
			
		} else if(nbrSession == 1) {
			theSession = Evaluation.SESSION.DEUXIEME;
		} else {
			theSession = null;
		}

		//List<Evaluation> eval = new ArrayList<>();
		
		if(theSession != null) {
			for(int i=0 ; i < etudiantOfModule.size() ; i++) {

				Evaluation evaluation = new Evaluation();
				
				evaluation.setId(evaluationDAO.generateId() + 1);
				evaluation.setEtudiant(etudiantDAO.findOne(etudiantOfModule.get(i)));
				evaluation.setModule(module);
//				Short result = new Short("0");
				evaluation.setResultat( 0 ); 
				evaluation.setSession(theSession);	
				

				System.out.println(evaluation.toString());
				evaluationDAO.save(evaluation);
			}
			
			
		}

		
		return "redirect:/module/liste";
	}
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////
//   BASIC FUCTIONS                                                    //
/////////////////////////////////////////////////////////////////////////
	
	
	public Integer getSessionOfEvaluation(String code) {
		
		System.out.printf( "*["+  this.getClass().getSimpleName() + "]"  +  "[getSessionOfEvaluation]"  +  "[]" );
		
		List<Evaluation.SESSION> allSessionOfModule = new ArrayList<>();
		allSessionOfModule = evaluationDAO.getSessionsOfModule(code);
		
		return allSessionOfModule.size();
	}
	

}





























































