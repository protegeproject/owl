package edu.stanford.smi.protegex.owl.server;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.Server_Test;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

public class OwlServerTestCase extends AbstractJenaTestCase {
  private static final String USER = "Timothy Redmond";
  private static final String PASSWORD = "timothy";
  private static final String PROJECT_NAME = "Pizza";
  
  
  public void setUp() throws Exception {
    super.setUp();
    Server_Test.setMetaProject("junit/projects/metaproject.pprj");
    if (!Server_Test.startServer()) {
      return;
    }
  }
  
  public void testGetProject() {
    if (!Server_Test.isServerRunning()) {
      return;
    }
    Project p = RemoteProjectManager.getInstance().getProject(Server_Test.HOST, USER, PASSWORD, PROJECT_NAME, true);
    assertNotNull(p);
    KnowledgeBase kb = p.getKnowledgeBase();
    Cls cls = kb.getCls("Pizza");
    System.out.println("found " + cls + " with frame id = " + cls.getFrameID());
    assertNotNull(cls);
    p.dispose();
  }

}
