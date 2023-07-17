
package jb.selfregulation;

import jb.selfregulation.application.Configurable;

public interface LatticeStatusListener extends Configurable
{
    void latticeChangedEvent(Lattice aLattice);
}
