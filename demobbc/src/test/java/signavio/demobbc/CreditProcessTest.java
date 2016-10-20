package signavio.demobbc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugProcessEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import signavio.Prospect;

public class CreditProcessTest extends JbpmJUnitBaseTestCase {

	private RuntimeManager runtimeManager;

	private RuntimeEngine runtimeEngine;

	private KieSession kieSession;

	public CreditProcessTest() {
		// Setup the datasource and enable persistence.
		super(true, true);
	}

	@Before
	public void init() {
		runtimeManager = createPpiRuntimeManager("creditprocess.bpmn2");
		
		
		runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
		kieSession = runtimeEngine.getKieSession();
		//Register mock HT WIH to be able to complete process.
		//kieSession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
		kieSession.addEventListener(new DebugAgendaEventListener());
		kieSession.addEventListener(new DebugRuleRuntimeEventListener());
		kieSession.addEventListener(new DebugProcessEventListener());
	}

	protected RuntimeManager createPpiRuntimeManager(String... process) {
		/*
		 * Using LinkedHashMap so we iterate over the map in insertion order.
		 * This is needed as the process depends on types declared in the DRL.
		 */
		Map<String, ResourceType> resources = new LinkedHashMap<String, ResourceType>();
		resources.put("rules.drl", ResourceType.DRL);
		resources.put("creditprocess.bpmn2", ResourceType.BPMN2);
		return createRuntimeManager(Strategy.PROCESS_INSTANCE, resources);
	}

	@After
	public void destroy() {
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
	}

	@Test
	public void testAcceptCreditCardApplication() {
		Object input = getAcceptFact(kieSession);
		
		Prospect prospect = new Prospect("Duncan", "1234", "ddoyle@redhat.com");
		
		
		Map<String, Object> params = new HashMap<>();
		params.put("input", input);
		params.put("prospect", prospect);
		
		ProcessInstance pInstance = kieSession.startProcess("creditprocess", params);
		
		
	}
	
	//@Test
	public void testRejectCreditCardApplication() {
		Object input = getRejectFact(kieSession);
		Map<String, Object> params = new HashMap<>();
		params.put("input", input);
		
		ProcessInstance pInstance = kieSession.startProcess("creditprocess", params);
		
	}
	
	
	private Object getRejectFact(KieSession kieSession) {
		FactType inputType = kieSession.getKieBase().getFactType("defaultpkg", "Input");
		Object input = null;
		try {
			input = inputType.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputType.set(input, "fICO", new BigDecimal(600));
		inputType.set(input, "bankruptcies", false);
		inputType.set(input, "medical", new BigDecimal(500));
		inputType.set(input, "defaults", true);
		inputType.set(input, "rentOrMortgage", new BigDecimal(800));
		inputType.set(input, "income", new BigDecimal(4500));
		inputType.set(input, "settlements", false);
		inputType.set(input, "consumerDebt", new BigDecimal(240));
		return input;
	}
	
	private Object getAcceptFact(KieSession kieSession) {
		FactType inputType = kieSession.getKieBase().getFactType("defaultpkg", "Input");
		Object input = null;
		try {
			input = inputType.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputType.set(input, "fICO", new BigDecimal(800));
		inputType.set(input, "bankruptcies", false);
		inputType.set(input, "medical", new BigDecimal(320));
		inputType.set(input, "defaults", false);
		inputType.set(input, "rentOrMortgage", new BigDecimal(2200));
		inputType.set(input, "income", new BigDecimal(14000));
		inputType.set(input, "settlements", false);
		inputType.set(input, "consumerDebt", new BigDecimal(0));
		return input;
	}

}
