
package jb.selfregulation.application;

import java.util.Properties;


public interface Configurable
{    
    String getBase();
    
    void loadConfig(String aBase, Properties prop);
    
    void setup(SystemState aState);
}
