/*	
====================================================================================
2011 | Jonathan Baker  | JazzGen  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ JazzGen {

	jazzBass{
	
	{
			(8*tempMult).do{arg bc; //bass counter
//						"bass: ".post;
//						probs[bc][0].postln;
			if(solo<0,{
			
			// Standard Playing
			num = 	if(tempMult<2,{
					 if(bc<5,{bc},{8-bc})
				},{	if(bc<5,{bc},
				
						{if(bc<8, {8-bc},
						
							{if(bc<13, {(-8)+bc},
							
							{16-bc})
						})
					})
				});
		
			bassNote = pentMaj[num];
				
				if((((probs[bc][0]*tempMult).coin)||(bc==0)), {
				
					if(((probs[bc][0]**6).coin)&&(tempoChange==false),{
					bass = Synth(\bass, [\note, scale[root+bassNote]-bassOct]);
					(bassTimes[bc]*0.667).wait;
					bass.free;
					bass = Synth(\bass, [\note, scale[root+bassNote+1]-bassOct]);
					(bassTimes[bc]*0.333).wait;
					},{
					bass = Synth(\bass, [\note, scale[root+bassNote]-bassOct]);
					bassTimes[bc].wait;
					})
				},{
					bassTimes[bc].wait;
				});
			
			},{
				// Solo Playing
				
				if(solo==0,{
								
					//calculate individual score for solo
					bs = (1.0-(scores[0]/gaussianThresh));
					
					if(soloPause==false,{
						bsNote = soloNote[bc];
						if(bs.coin, {
							
							if(bs.coin,{
							

								//probability for triplets increases on slower tempos
								trip = ((1.0-((min(bps,0.72)-0.4)/0.3501)).squared);

								if((trip.coin)&&(bs.coin),{
								
									bass = Synth(\bass, [\note, scale[root+bsNote]-bassOct, \ampMult,soloistAmp]);
									(bassTimes[bc]*0.333).wait;

									bass = Synth(\bass, [\note, scale[root+(bsNote+noteVar.choose;)]-bassOct, \ampMult,soloistAmp]);
									(bassTimes[bc]*0.333).wait;

									bass = Synth(\bass, [\note, scale[root+(bsNote+[0,1].choose;)]-bassOct, \ampMult,soloistAmp]);
									(bassTimes[bc]*0.334).wait;
									},{
								
									(bassTimes[bc]*0.667).wait;

									bass = Synth(\bass, [\note, scale[root+(bsNote)]-bassOct, \ampMult,soloistAmp]);
									(bassTimes[bc]*0.333).wait;
									})
								},{
								bass = Synth(\bass, [\note, scale[root+bsNote]-bassOct, \ampMult,soloistAmp]);
								(bassTimes[bc]*0.667).wait;
								bass = Synth(\bass, [\note, scale[root+(bsNote+noteVar.choose)]-bassOct, \ampMult,soloistAmp]);
								(bassTimes[bc]*0.333).wait;
								})
							},{
							if(sPause.coin, {soloPause = true;sPause = 0.01},{sPause = sPause*1.2});
							bRel = if(soloPause, {(min((8-bc),3))*0.5},{0.5});
							bass = Synth(\bass, [\note, scale[root+bsNote]-bassOct,\bassRel,bRel, \pluckRel, (bRel*0.55),\formFreq,rrand(12000,17000), \ampMult,soloistAmp]);
							
							bassTimes[bc].wait;
							}
						)
					},{
						bassTimes[bc].wait;
					})

						
			},{

			//============= Accompaniment ========================
	
				//bs = (1.0-(scores[0]/gaussianThresh));
				if(bc==0,{
					bRel = rrand(1,1.5);
					bass = Synth(\bass, [\note, scale[root]-bassOct;,\bassRel, bRel, \pluckRel, (bRel*0.55),\formFreq,rrand(12000,17000),\ampMult,soloAcAmp]);
					bassTimes[bc].wait;
				},{
					if((bc>5)&&(probs[bc][0].squared.coin),{
					
						bass = Synth(\bass, [\note, scale[root+soloNote[bc]]-bassOct,\ampMult,soloAcAmp]);
						bassTimes[bc].wait;
					
					},{
						
						bassTimes[bc].wait;
						
					})
				})
			});
		})
		}
	}.fork(tempo);
		



	
	}




}