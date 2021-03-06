/*
 *  Copyright 2015-present Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.railmodel;

import static org.joda.railmodel.Stations.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * Calculates journey times for SW London with Crossrail 2 in place.
 */
public class Crossrail2StreathamSWLondonModel extends BaseLondonModel {

  public static void main(String[] args) throws Exception {
    Crossrail2StreathamSWLondonModel model = new Crossrail2StreathamSWLondonModel();
    ImmutableList<Station> starts = ImmutableList.of(
        CSS, LHD, EPS, SNL, WCP, MOT, SHP, FLW, KNG, HMC, SUR, NEM, RAY, WIM, EAD, UMD, USW, UTB, BAL, SRH, STE);
    ImmutableList<Station> ends = ImmutableList.of(
        VIC, TCR, EUS, AGL, WAT, UGP, UOX, BDS, CHX, ULS, UGS, UWS, UBS, UWM, UTM, ZFD, UBH, LBG, UBK, MOG, UOS, UHL, UCL, USP, CWF);

    List<String> output = new ArrayList<>();
    output.add("Modelling for SW London with Crossrail 2 via Streatham" + NEWLINE);
    output.add("======================================================" + NEWLINE);
    output.add("This uses CR2 via Tooting (mainline) and Streatham, with best efforts guesses of interchange times." + NEWLINE);
    output.add("This route is promoted by various groups in Streatham." + NEWLINE);
    output.add("It adds at least 6 minutes to all journeys on Crossrail 2 between Wimbledon and Clapham Junction." + NEWLINE);
    output.add("As such, no matter what benefits it gives Streatham, it simply will not happen." + NEWLINE);
    output.add(NEWLINE);
    appendDocs(output);
    appendTotals(output, starts, ends, model);
    appendSeparator(output);
    for (Iterator<Station> it = starts.iterator(); it.hasNext();) {
      Station start = it.next();
      for (Station end : ends) {
        String explain = model.explain(start, end);
        output.add(explain);
        output.add(NEWLINE);
      }
      if (it.hasNext()) {
        appendSeparator(output);
      }
    }
    appendStations(output);
    output.add(NEWLINE);
    output.add("Feel free to send a pull request for errors and enhancments!" + NEWLINE);

    File file = new File("CR2-Streatham-SWLondon.md");
    String result = Joiner.on("").join(output);
    Files.write(result, file, StandardCharsets.UTF_8);
    System.out.println(result);
  }

  Crossrail2StreathamSWLondonModel() {
    setup();
  }

  private void setup() {
    // WAT
    addRoute(SWML_TWI_WAT_4);
    addRoute(SWML_LHD_WAT_4);
    addRoute(SWML_SUR_WAT_FAST_6);
    addRoute(SWML_SUR_WAT_SLOW_4);
    // CR2
    // CLJ-CKR known as 3 mins
    // CKR-VIC and VIC-TCR are similar distances
    Route wimagl = Route.of(
        "CR2",
        "CR2",
        30,
        stations(WIM, TOO, STE, CLJ),
        times(4, 3, 6),
        CR2_CLJ_AGL);
    Route rayagl = Route.of(
        "CR2",
        "CR2",
        20,
        stations(RAY, WIM),
        times(4),
        wimagl);
    Route nemagl = Route.of(
        "CR2",
        "CR2",
        10,
        stations(NEM, RAY),
        times(3),
        rayagl);
    Route motagl = Route.of(
        "CR2",
        "CR2",
        10,
        stations(MOT, RAY),
        times(3),
        rayagl);
    Route kngagl = Route.of(
        "CR2",
        "CR2",
        6,
        stations(KNG, NEM),
        times(7),
        nemagl);
    Route shpagl = Route.of(
        "CR2",
        "CR2",
        4,
        stations(SHP, FLW, KNG),
        times(13, 10),
        kngagl);
    Route hmcagl = Route.of(
        "CR2",
        "CR2",
        4,
        stations(HMC, SUR, NEM),
        times(8, 5),
        nemagl);
    Route cssagl = Route.of(
        "CR2",
        "CR2",
        4,
        stations(CSS, MOT),
        times(11),
        motagl);
    Route epsagl = Route.of(
        "CR2",
        "CR2",
        6,
        stations(EPS, SNL, WCP, MOT),
        times(5, 3, 3),
        motagl);
    addRoute(shpagl);
    addRoute(kngagl);
    addRoute(hmcagl);
    addRoute(cssagl);
    addRoute(epsagl);

    // Southern
    addRoute(SOUTHERN_BAL_VIC);
    addRoute(SOUTHERN_SRH_VIC);
    addRoute(THAMESLINK_EPH_ZFD);
    addRoute(THAMESLINK_STE_ZFD);
    addRoute(SOUTHERN_STE_LBG);
    addRoute(SOUTHEAST_HNH_VIC);

    // Tube lines
    addRoute(NORTHERN_CITY_NB);
    addRoute(NORTHERN_CITY_SB);
    addRoute(NORTHERN_WEST_NB);
    addRoute(NORTHERN_WEST_SB);
    addRoute(VICTORIA_NB);
    addRoute(BAKERLOO_NB);
    addRoute(JUBILEE_EB);
    addRoute(JUBILEE_NB);
    addRoute(WNC_NB);
    addRoute(DISTRICT_EB);
    addRoute(CENTRAL_EB);
    addRoute(CENTRAL_WB);
    addRoute(CR1_EB);
    addRoute(CR1_WB);

    // change CR2 to WAT at SUR, assume 6 tracks between Surbiton and New Malden
    // gaps between fast trains work out at 4-6 mins with 10min gap twice an hour
    // assume a sensible timetable minimises interchange time
    addChange(Change.of(SUR, hmcagl, SWML_SUR_WAT_FAST_6, 3, 5));
    addChange(Change.of(SUR, hmcagl, SWML_SUR_WAT_SLOW_4, 3, 5));

    // change CR2 to WAT at RAY, assume 8tph at gaps of 6 and 9 minutes
    Change xraycr2wat = Change.of(RAY, rayagl, SWML_RAY_WAT_8, 1, 9);
    addChange(xraycr2wat);
    // change WAT to CR2 at RAY, 20tph at gaps of 2 and 4 minutes
    Change xraywatcr2 = Change.of(RAY, SWML_RAY_WAT_8, rayagl, 1, 5);
    addChange(xraywatcr2);

    // change at Wimbledon
    Change xwimwatcr2 = Change.of(WIM, SWML_WIM_WAT_12, wimagl, 4, 6);
    Change xwimcr2wat = Change.of(WIM, wimagl, SWML_WIM_WAT_12, 4, 10);
    addChange(xwimwatcr2);
    addChange(xwimcr2wat);  // gaps of 3 to 6 mins

    // change at Balham
    addChange(Change.of(BAL, SOUTHERN_BAL_VIC, NORTHERN_CITY_NB, 4, 6));

    // change at Streatham
    addChange(Change.of(STE, wimagl, THAMESLINK_STE_ZFD, 3, 5));

    // change at Clapham Junction
    Change xcljwatcr2 = Change.of(CLJ, SWML_CLJ_WAT_18, CR2_CLJ_AGL, 4, 6);
    addChange(xcljwatcr2);
    addChange(Change.of(CLJ, SWML_CLJ_WAT_18, SOUTHERN_BAL_VIC, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, CR2_CLJ_AGL, 4, 6));
    addChange(Change.of(CLJ, SOUTHERN_BAL_VIC, SWML_CLJ_WAT_18, 4, 6));
    addChange(Change.of(CLJ, CR2_CLJ_AGL, SWML_CLJ_WAT_18, 4, 6));

    // prefer change at RAY to WIM/CLJ if choice
    addPreferredChange(xraywatcr2, xwimwatcr2);
//    addPreferredChange(xraywatcr2, xcljwatcr2);
    addPreferredChange(xraycr2wat, xwimcr2wat);

    // change at Victoria
    addChange(Change.of(VIC, CR2_VIC_AGL, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, CR2_VIC_AGL, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, VICTORIA_NB, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHERN_BAL_VIC, VICTORIA_NB, 4, 6));
    addChange(Change.of(VIC, SOUTHEAST_HNH_VIC, DISTRICT_EB, 4, 6));
    addChange(Change.of(VIC, SOUTHEAST_HNH_VIC, VICTORIA_NB, 4, 6));

    // change at TCR
    addChange(Change.of(TCR, CR2_VIC_AGL, CR1_EB, 3, 5));
    addChange(Change.of(TCR, CR2_VIC_AGL, CR1_WB, 3, 5));
    addChange(Change.of(TCR, CR2_VIC_AGL, CENTRAL_EB, 4, 6));
    addChange(Change.of(TCR, CR2_VIC_AGL, NORTHERN_WEST_NB, 3, 5));
    addChange(Change.of(TCR, CR2_VIC_AGL, NORTHERN_WEST_SB, 3, 5));

    // change at Euston (pointless, might as well change at Angel)
    // addChange(Change.of(EUS, wimagl, unortherncitysb, 4, 8));

    // change at Angel
    addChange(Change.of(AGL, CR2_VIC_AGL, NORTHERN_CITY_SB, 3, 6));

    commonChanges(NORTHERN_CITY_NB);
  }

}
