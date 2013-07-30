package it.uniurb.disbef.virtualsense.basestation.gui;



import it.uniurb.disbef.virtualsense.basestation.Node;
import it.uniurb.disbef.virtualsense.basestation.Packet;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * 
 * @author  lattanzi
 */
public class NodesPanel extends javax.swing.JPanel implements MouseListener , MouseMotionListener{
    private Hashtable<Short, Node> tableNodes;
    //private DataMsg actualMessage;
    private Node[] nodes;
    //private Node sink;
    private java.text.DecimalFormat format = new java.text.DecimalFormat("0.00");
    
    public double maxLati;
    public double minLati;
    public double maxLongi;
    public double minLongi;
    private int screenHeight = 1100;
    private int screenWidth = 1150;
    //private NodeFlow flowToPlot = null;
    private int maxFlow = 0;
    private boolean plottingFlow = false;
    private boolean routed = false;    
    private BufferedImage image;
    private BufferedImage actualGraph;
    private GUI gui;
    private short nodeToShowInfo = -1;
    private Packet lastPacket = null;
    private int sinkX = 245;
    private short sinkY = 545;

    /** Creates new form NewJPanel */
    public NodesPanel(Hashtable<Short, Node> nodes, GUI gui) {
    	this.tableNodes = nodes;
    	this.gui = gui;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
    	try {                
    		//System.out.println("Drawing image");
            image = ImageIO.read(new File("image.png"));
         } catch (IOException ex) {
             System.out.println(ex);
         }
    
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        addMouseListener(this);
        addMouseMotionListener(this);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    public void paintComponent(Graphics g){
		super.paintComponent(g); 
				if(image != null)
					g.drawImage(image, 0, 0, 745,720,null); // see javadoc for more info on the parameters 
		 		g.setColor(Color.LIGHT_GRAY);
                //g.fillRect(0, 0, this.getWidth(), this.getHeight());
                //if(this.plottingFlow)
                //    this.drawFlow(g);
                this.drawNodes(g);
                this.drawLinks(g);
                this.drawGraph(g);
                
                
        
    }
    
    private void drawGraph(Graphics g){
    	if(actualGraph != null)
    		g.drawImage(actualGraph, 750,0, 550,700, this);
    }
    
    private void drawNodes(Graphics g){
        // draw nodes
        if(this.tableNodes != null){
            this.nodes = new Node[this.tableNodes.size()];
            Enumeration e = this.tableNodes.elements();

            while(e.hasMoreElements()){

                Node nn = (Node)e.nextElement();
               /* if(nn.type == Node.SINK)
                    g.setColor(Color.CYAN);
                else*/
                g.setColor(new Color(135,206,250,100));                            
                //String address = nn.getShortStringID();
                String address = ""+nn.ID;
                // draw circle around nodes
                if(nn.ID != 0){
                	g.fillOval(nn.xLocation, nn.yLocation, 30, 30);                	
                }
                if(this.gui.showsNodeInfoCheckBoxMenuItem.isSelected() && nn.ID == this.nodeToShowInfo) {
                	g.setColor(new Color(135,135,250,255)); 
                	g.fillRect(nn.xLocation+20, nn.yLocation+20, 80, 50);
                	g.setColor(Color.black);
                	g.drawRect(nn.xLocation+20, nn.yLocation+20, 80, 50);
                    g.drawString("sent: "+nn.myPackets.size(), nn.xLocation+25, nn.yLocation+35);
                    g.drawString("routed: "+nn.routedPacket, nn.xLocation+25, nn.yLocation+50);                    
                    
                }
                g.setColor(new Color(135,206,250,100)); 
                
                /*if(SentillaBaseStationView.packetSentCheckBox.getState())
                     g.drawString(nn.arrived+"/"+nn.sent, nn.x+40, nn.y+90);
                if(SentillaBaseStationView.packetRoutedCheckBox.getState())
                     g.drawString(""+nn.forwarded, nn.x+90, nn.y+90);
                if(SentillaBaseStationView.tempCheckBox.getState())
                     g.drawString("T: "+format.format(nn.temp), nn.x+40, nn.y+105);
                if(SentillaBaseStationView.voltageCheckBox.getState())
                     g.drawString("V: "+format.format(nn.v), nn.x+40, nn.y+120);*/
              
            }
            // draw sink
            
            g.fillOval(this.sinkX, this.sinkY, 30, 30);   
        }
    }
    
    private void drawLinks(Graphics g){
        //draws link (message)
        if(this.lastPacket != null){
            //g.setColor(Color.BLACK);
            short sendingNode = this.lastPacket.sender;
            Node n = this.tableNodes.get(new Short(sendingNode));                   
            g.setColor(Color.RED);
            g.fillOval(n.xLocation, n.yLocation, 20, 20);
            //System.out.println("Found node: "+index);
            /*if(this.actualMessage.destAddress == 0xFFFFFFFFFFFFFFFFL){
                g.drawOval(n.x+40, n.y+40, 50, 50);
                g.drawOval(n.x+30, n.y+30, 70, 70);
                g.drawOval(n.x+20, n.y+20, 90, 90);
            }*/
            if(this.lastPacket.route == 0){                        // direct to the sink                    
                    g.drawLine(n.xLocation+5, n.yLocation+5, this.sinkX+5, this.sinkY+5);
                    g.fillOval(this.sinkX, this.sinkY, 20 , 20);
            }else {
            		LinkedList<Short> hops = this.lastPacket.getHospIndexes();
            		//System.out.println("This packet has "+hops.size()+" hops");
            		// find the closer 
            		int size = hops.size();
                    for(int i = 0; i < size; i++){
                    	// find closer node
                    	
                    	Node nn = findTheCloser(n, hops);
                    	//System.out.println("drawing link from "+n.ID+" to "+nn.ID);
                        g.drawLine(n.xLocation+5, n.yLocation+5, nn.xLocation+5, nn.yLocation+5);
                        g.fillOval(nn.xLocation, nn.yLocation, 20 , 20);
                        hops.remove(new Short(nn.ID));
                        n = nn;
                        
                    }
                    g.drawLine(n.xLocation+5, n.yLocation+5, this.sinkX, this.sinkY);
                    g.fillOval(this.sinkX, this.sinkY, 20 , 20);
                }
            }

        }
    private Node findTheCloser(Node n, LinkedList<Short> hops) {
		Node ret = findNode(hops.get(0));
		double distance = ret.getDistance(n);
		for(int i = 1; i < hops.size(); i++){
			Node tmp = findNode(hops.get(i));
			if(tmp.getDistance(n) < distance){
				distance = tmp.getDistance(n);
				ret = tmp;
			}
		}		
		return ret;
	}
    
    public void updatePacket(Packet p){
        this.lastPacket = p;
        this.repaint();
        
    } 
	 
    
    /*private void drawFlow(Graphics g){
        if(this.flowToPlot != null){
            Enumeration<LinkFlow> en;
            en = this.flowToPlot.getLinkFlows();
            while(en.hasMoreElements()){
                if(!this.routed)
                g.setColor(Color.MAGENTA);
                LinkFlow l = en.nextElement();
                Node src = findNodeByNumber(l.srcId);
                Node dest = findNodeByNumber(l.destId);
                if(this.routed){
                    g.setColor(Color.GREEN);
                    if(src.getNumber() == this.flowToPlot.getNodeID())
                        g.setColor(Color.RED);
                }
                // drawing flow 
                	//generate polygon points
                int x[] = new int[4];
                int y[] = new int[4];
                x[0] = src.x+60;
                x[1] = src.x+60;
                x[2] = dest.x+60;
                x[3] = dest.x+60;

                y[0] = src.y+60;//(int);(this.nodes[this.edges[i].Sn].y*SCALE);
                y[1] = src.y+60;//(int);(this.nodes[this.edges[i].Sn].y*SCALE);
                y[2] = dest.y+60;//(int);(this.nodes[this.edges[i].Dn].y*SCALE);
                y[3] = dest.y+60;//(int);(this.nodes[this.edges[i].Dn].y*SCALE);

                double DyDx = (double)(y[2]-y[0])/(double)(x[2]-x[0]);
                DyDx = Math.cos(Math.atan(DyDx));
                //System.out.println("DDDDD DyDx "+DyDx);

                
                double flowRap = ((double)l.value)/this.maxFlow;
                //double flowRap = Math.log10(l.value);///this.maxFlowlow;

                int dx = (int)((1-DyDx)*(flowRap)*10);
                int dy = (int)((DyDx)*(flowRap)*10);

                if(dx == 0)
                        dx =2;
                if(dy == 0)
                        dy = 2; 
                //System.out.println("Dx "+dx+" Dy "+dy+" FlowRap "+flowRap+" MaxF "+this.maxFlow);
                //int dx = (int)((1-DyDx)*5);
                //int dy = (int)((DyDx)*5);                
                x[0] = x[0]- dx;
                x[1] = x[1]+ dx;
                x[2] = x[2]+ dx;
                x[3] = x[3]-dx;

                y[0] = y[0]- dy;
                y[1] = y[1]+ dy;
                y[2] = y[2]+ dy;
                y[3] = y[3]- dy;
                g.fillPolygon(x,y, 4);
                //System.out.println("Flow between "+src.getNumber()+"x: "+src.x+" and "+dest.getNumber()+" x: "+dest.x+" done ");
            }
        }
    }
    
    public void plotFlow(NodeFlow flow, boolean routed, int maxFlow){
        this.flowToPlot = flow;
        this.plottingFlow = true;
        this.routed = routed;
        this.maxFlow = maxFlow;
        System.out.println("Plotting "+(this.routed?"routed":"generated" )+" flow of "+this.flowToPlot.getNodeID());
        this.flowToPlot.printFlow();
        System.out.println("----------------");
        this.repaint();
    }
    
    public void updateMessage(DataMsg m){
        this.actualMessage = m;
        
    } */
    
    /*public void updateNodes(Hashtable<Long, Node> t){
        this.tableNodes = t;
        Enumeration e = this.tableNodes.elements();
        //System.out.println("Diff lat "+(this.maxLati-this.minLati));
        //System.out.println("Diff lon "+(this.maxLongi-this.minLongi));
        double maxD = this.maxLati-this.minLati;
        if((this.maxLongi-this.minLongi) > maxD)
            maxD = this.maxLongi-this.minLongi;
        while(e.hasMoreElements()){
            Node n = (Node)e.nextElement();
            n.y = ((int)(n.getLatitude()*this.screenHeight/this.maxLati))-(int)this.minLati*2;//(int)(((n.getLatitude()-this.minLati)/maxD)*this.screenHeight); //screenHeight
            //System.out.println(" sh "+this.screenHeight+" mL "+this.minLati);
            //System.out.println("Node "+n.getShortStringID()+" lx "+(n.getLatitude()-this.minLati));
            //System.out.println("Node "+n.getShortStringID()+" y "+n.y);
            n.x = ((int)(n.getLongitude()*this.screenWidth/this.maxLongi))-(int)this.minLongi/3;//(int)(((n.getLongitude()-this.minLongi)/maxD)*this.screenWidth); //screenWidth
            //System.out.println(" sw "+this.screenWidth+" mLong "+this.minLongi);
           // System.out.println("Node "+n.getShortStringID()+" x "+n.x);
            
        }
    }    */
    
    public Node findNode(short id){        
        
        Node ret = null;
        Enumeration e = this.tableNodes.elements();
        while(e.hasMoreElements()){
            Node nn = (Node)e.nextElement();            
            if(nn.ID == id){
                ret = nn;
                break;
            }
        }
        return ret;        
    }
    public Node findNodeByPosition(Point p){        
        
        Node ret = null;
        double distance = 50; // ignore distance over 50 (null return)
        Enumeration e = this.tableNodes.elements();
        while(e.hasMoreElements()){
            Node nn = (Node)e.nextElement();     
            double newDistance = p.distance(nn.xLocation, nn.yLocation);
            if(newDistance < distance){
                ret = nn;
                distance = newDistance;
                //System.out.println("last distance = "+distance);
            }
        }
        return ret;        
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = e.getX();
		int y = e.getY();
		Node nn  = findNodeByPosition(new Point(x,y));
		if(nn != null){
			System.out.println("Clicked to "+x+" "+y);
			System.out.println("Selected node is "+nn.ID);
		
			// choose what to plot
			String[] possibilities = {"Counter", "Noise", "CO2", "People", "Temp", "Pressure", "Light"};	
			Vector<String> ps = new Vector<String>();
			for(int i = 0; i < possibilities.length; i++)
				if(nn.hasCapability(possibilities[i]))
					ps.add(possibilities[i]);
			
			Object[] realPossibilities = ps.toArray();
			String s = (String)JOptionPane.showInputDialog(
		                    this,
		                    "Select the potting value",
		                    "Plotting selection Dialog",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    realPossibilities,
		                    "Counter");
		
			if ((s != null) && (s.length() > 0)) {
				TimeSerieGraph timeSerie = new TimeSerieGraph();
				actualGraph = timeSerie.createTimeSeriesXYChart(nn, s);
				this.repaint();
			}
		}	
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		Node nn  = findNodeByPosition(new Point(e.getX(),e.getY()));
		if(nn != null){
			nodeToShowInfo = nn.ID;
		}else 
			nodeToShowInfo = -1;
		this.repaint();
		
		
	}
    
    
}